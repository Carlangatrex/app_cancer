export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (request.method === 'OPTIONS') {
      return new Response(null, { headers: { 'Access-Control-Allow-Origin': '*', 'Access-Control-Allow-Methods': 'POST, OPTIONS', 'Access-Control-Allow-Headers': 'Content-Type' } });
    }
    if (!['/api/gemini', '/api/gemini/stream'].includes(url.pathname)) return new Response('Not found', { status: 404 });
    if (request.method !== 'POST') return new Response('Method not allowed', { status: 405 });
    const body = await request.text();
    const isStream = url.pathname.includes('stream');
    const model = url.searchParams.get('model') || 'gemini-flash-lite-latest';
    const endpoint = https://generativelanguage.googleapis.com/v1beta/models/:;
    const resp = await fetch(endpoint, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body });
    const h = new Headers(resp.headers); h.set('Access-Control-Allow-Origin', '*');
    return new Response(resp.body, { status: resp.status, headers: h });
  }
}
