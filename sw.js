// Service Worker de Alma. Estrategias:
// - Navegaciones (HTML): network-first, y si el servidor no responde, la versión en caché.
// - Assets estáticos precacheados (icono, svg, manifest): cache-first.
// - Todo lo demás (reportes de test, peticiones a la API de Gemini): pasa a red, sin cachear.
// Así, en desarrollo siempre se ve el HTML recién editado, y la API nunca se cachea.

const CACHE = 'alma-v2';
const PRECACHE = [
  './test_gemini.html',
  './icon-192.png',
  './icon-512.png',
  './gemini-svg.svg',
  './manifest.json'
];

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches.open(CACHE)
      .then((cache) => cache.addAll(PRECACHE))
      .then(() => self.skipWaiting())
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(
    caches.keys()
      .then((claves) => Promise.all(
        claves.filter((c) => c !== CACHE).map((c) => caches.delete(c))
      ))
      .then(() => self.clients.claim())
  );
});

self.addEventListener('fetch', (event) => {
  const req = event.request;
  if (req.method !== 'GET') return;

  // Navegación: lo más reciente si estás online; la copia guardada si no.
  if (req.mode === 'navigate') {
    event.respondWith(
      fetch(req)
        .then((res) => {
          const copia = res.clone();
          caches.open(CACHE).then((cache) => cache.put('./test_gemini.html', copia)).catch(() => {});
          return res;
        })
        .catch(() => caches.match('./test_gemini.html'))
    );
    return;
  }

  // Solo cacheamos assets ya precacheados; nada más se guarda jamás.
  if (req.url.startsWith(self.location.origin) && PRECACHE.some((r) => req.url.endsWith(r))) {
    event.respondWith(
      caches.match(req).then((guardado) => guardado || fetch(req))
    );
  }
  // El resto (API, reportes de test, etc.) se deja ir a la red.
});