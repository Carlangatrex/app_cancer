// Proxy de Gemini para el frontend servido por este mismo Worker ([assets] en
// wrangler.toml). La API key viene del secret GEMINI_API_KEY del Worker.
// CORS: solo se aceptan peticiones del propio sitio (el worker sirve el
// frontend) y de orígenes declarados en la variable SITE_ORIGIN (lista
// separada por comas, opcional).

const TARGET_BASE = 'https://generativelanguage.googleapis.com/v1beta/models/';

function isAllowedOrigin(origin, request, env) {
  // Sin cabecera Origin: petición same-origin (navegador) o cliente no-navegador.
  if (!origin) return true;
  try {
    if (new URL(origin).host === new URL(request.url).host) return true; // el propio worker
  } catch (_) { /* Origin malformado */ }
  const extra = (env.SITE_ORIGIN || '').split(',').map((s) => s.trim()).filter(Boolean);
  return extra.includes(origin);
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    const origin = request.headers.get('Origin');

    if (url.pathname !== '/api/gemini' && url.pathname !== '/api/gemini/stream') {
      return new Response('Not found', { status: 404 });
    }
    if (request.method !== 'POST' && request.method !== 'OPTIONS') {
      return new Response('Method not allowed', { status: 405 });
    }
    if (!isAllowedOrigin(origin, request, env)) {
      return new Response('Forbidden', { status: 403 });
    }

    const corsHeaders = {
      'Access-Control-Allow-Origin': origin || '*',
      'Access-Control-Allow-Methods': 'POST, OPTIONS',
      'Access-Control-Allow-Headers': 'Content-Type',
    };
    if (request.method === 'OPTIONS') return new Response(null, { status: 204, headers: corsHeaders });

    const body = await request.text();
    const isStream = url.pathname.includes('stream');
    const model = url.searchParams.get('model') || 'gemini-flash-lite-latest';
    const endpoint = isStream
      ? `${TARGET_BASE}${model}:streamGenerateContent?alt=sse&key=${env.GEMINI_API_KEY}`
      : `${TARGET_BASE}${model}:generateContent?key=${env.GEMINI_API_KEY}`;
    const resp = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body,
    });
    const respHeaders = new Headers(resp.headers);
    for (const [k, v] of Object.entries(corsHeaders)) respHeaders.set(k, v);
    return new Response(resp.body, {
      status: resp.status,
      headers: respHeaders,
    });
  },
};
