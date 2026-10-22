// Genera los audios de voz de los acompañantes con edge-tts.
//
// Uso:
//   node tools/generar_audios.mjs
//
// Requisitos: Chrome instalado, Python con edge-tts (`python -m pip install edge-tts`).
//
// El guion (frases) se extrae del propio web/app.html ejecutándolo en un Chrome
// headless, para que nunca se desincronice del código. Los MP3 se guardan en
// web/audio/<acompanante>/<clave>.mp3 y el chat los reproduce automáticamente.

import { spawn, spawnSync } from 'node:child_process';
import os from 'node:os';
import fs from 'node:fs';
import path from 'node:path';
import { fileURLToPath } from 'node:url';

const RAIZ = path.resolve(path.dirname(fileURLToPath(import.meta.url)), '..');
const APP = path.join(RAIZ, 'web', 'app.html');
const SALIDA = path.join(RAIZ, 'web', 'audio');

const CHROME = process.env.CHROME || 'C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe';
const PYTHON = process.env.PYTHON || 'python';
const PORT = 9341;
const CONCURRENCIA = 5;

// Una voz neuronal distinta por acompañante (todas femeninas, tono humano).
const VOCES = {
  serena: 'es-MX-DaliaNeural',   // 🌿 cálida y serena
  directa: 'es-ES-XimenaNeural', // 🧭 directa y práctica
  amiga: 'es-CO-SalomeNeural',   // 🌙 cercana, como una amiga
  firme: 'es-ES-ElviraNeural'    // 🐻 firme y protectora
};

const profile = path.join(os.tmpdir(), 'opencode', 'cpAudio');
const chrome = spawn(CHROME, [
  '--headless=new', '--remote-debugging-port=' + PORT, '--remote-allow-origins=*',
  '--user-data-dir=' + profile, '--no-first-run', '--no-default-browser-check', 'about:blank'
], { stdio: 'ignore' });

let ws, id = 0;
const pending = new Map();
function send(m, p = {}) { return new Promise(res => { const i = ++id; pending.set(i, res); ws.send(JSON.stringify({ id: i, method: m, params: p })); }); }
async function evalJs(expression) {
  const r = await send('Runtime.evaluate', { expression, returnByValue: true, awaitPromise: true });
  if (r.result && r.result.exceptionDetails) throw new Error('Excepción en la página: ' + JSON.stringify(r.result.exceptionDetails));
  return r.result.result.value;
}
async function waitDevtools() { for (let i = 0; i < 80; i++) { try { const r = await fetch('http://127.0.0.1:' + PORT + '/json/version'); if (r.ok) return; } catch {} await new Promise(r => setTimeout(r, 250)); } throw new Error('No se pudo conectar con Chrome (DevTools).'); }

function limpiarVoz(texto) {
  return String(texto)
    .replace(/«|»/g, '"')
    .replace(/\s*\n+\s*/g, ' ')
    .replace(/\s+/g, ' ')
    .trim();
}

function generarClip(texto, voz, destino) {
  const r = spawnSync(PYTHON, ['-m', 'edge_tts', '--voice', voz, '--text', texto, '--write-media', destino], { encoding: 'utf8' });
  if (r.status !== 0) throw new Error('edge-tts falló para ' + path.basename(destino) + ': ' + (r.stderr || r.error));
}

async function main() {
  await waitDevtools();
  const list = await (await fetch('http://127.0.0.1:' + PORT + '/json/list')).json();
  const page = list.find(t => t.type === 'page');
  ws = new WebSocket(page.webSocketDebuggerUrl);
  ws.addEventListener('message', ev => { const m = JSON.parse(ev.data); if (m.id && pending.has(m.id)) { pending.get(m.id)(m); pending.delete(m.id); } });
  await new Promise(res => ws.addEventListener('open', res));
  await send('Runtime.enable'); await send('Page.enable');
  await send('Page.navigate', { url: 'file:///' + APP.replace(/\\/g, '/') });
  for (let i = 0; i < 80; i++) { try { const t = await evalJs('typeof guionVoz'); if (t === 'function') break; } catch {} await new Promise(r => setTimeout(r, 250)); }

  const crudo = await evalJs('JSON.stringify({base: guionVoz(), comps: COMPANEROS.map(function(c){return {id:c.id, seg:c.seguimientos};})})');
  const datos = JSON.parse(crudo);

  const trabajos = [];
  for (const c of datos.comps) {
    const voz = VOCES[c.id];
    if (!voz) { console.warn('Sin voz para el acompañante ' + c.id + ', se omite.'); continue; }
    const mapa = Object.assign({}, datos.base);
    c.seg.forEach(function (t, i) { mapa['seg_' + i] = t; });
    for (const clave in mapa) trabajos.push({ companion: c.id, voz, clave, texto: limpiarVoz(mapa[clave]) });
  }

  console.log('A generar: ' + trabajos.length + ' clips (' + datos.comps.length + ' voces).');

  let hechos = 0, fallos = 0, omitidos = 0;
  async function correr(j) {
    const dir = path.join(SALIDA, j.companion);
    fs.mkdirSync(dir, { recursive: true });
    const destino = path.join(dir, j.clave + '.mp3');
    if (process.env.FORZAR !== '1' && fs.existsSync(destino) && fs.statSync(destino).size > 500) {
      omitidos++;
      return;
    }
    try {
      await new Promise(res => setImmediate(res));
      generarClip(j.texto, j.voz, destino);
      hechos++;
      if (hechos % 10 === 0 || hechos === trabajos.length) console.log('  ' + hechos + '/' + trabajos.length);
    } catch (e) {
      fallos++;
      console.warn('  ! ' + e.message);
    }
  }

  let idx = 0;
  async function worker() { while (idx < trabajos.length) { const j = trabajos[idx++]; await correr(j); } }
  await Promise.all(Array.from({ length: CONCURRENCIA }, worker));

  console.log('Listo. Generados ' + hechos + ', omitidos ' + omitidos + ', fallos ' + fallos + '. Carpeta: ' + SALIDA);
}

try {
  await main();
} finally {
  try { ws && ws.close(); } catch {}
  chrome.kill();
  setTimeout(() => process.exit(0), 100);
}
