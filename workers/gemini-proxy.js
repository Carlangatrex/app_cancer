export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (request.method === 'OPTIONS') {
      return new Response(null, {
        headers: {
          'Access-Control-Allow-Origin': '*',
          'Access-Control-Allow-Methods': 'POST, OPTIONS',
          'Access-Control-Allow-Headers': 'Content-Type',
        },
      });
    }
    if (url.pathname !== '/api/gemini' && url.pathname !== '/api/gemini/stream') {
      return new Response('Not found', { status: 404 });
    }
    if (request.method !== 'POST') {
      return new Response('Method not allowed', { status: 405 });
    }
    const targetBase = 'https://generativelanguage.googleapis.com/v1beta/models/';
    const body = await request.text();
    const isStream = url.pathname.includes('stream');
    const model = url.searchParams.get('model') || 'gemini-flash-lite-latest';
    const endpoint = isStream
      ? `${targetBase}${model}:streamGenerateContent?alt=sse&key=${env.GEMINI_API_KEY}`
      : `${targetBase}${model}:generateContent?key=${env.GEMINI_API_KEY}`;
    const resp = await fetch(endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body,
    });
    const respHeaders = new Headers(resp.headers);
    respHeaders.set('Access-Control-Allow-Origin', '*');
    return new Response(resp.body, {
      status: resp.status,
      headers: respHeaders,
    });
  },
};