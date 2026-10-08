// Genera config.js a partir de la variable de entorno GEMINI_API_KEY de Netlify.
// config.js está en .gitignore: en local se usa el archivo real; en el build de
// Netlify se recrea sin haberlo versionado nunca.
const fs = require('fs');

const key = (process.env.GEMINI_API_KEY || '').trim();

fs.writeFileSync(
  'config.js',
  '// Generado por netlify-create-config.js en el build de Netlify.\n' +
    'window.GEMINI_API_KEY = ' + JSON.stringify(key) + ';\n'
);

if (!key) {
  console.error('AVISO: GEMINI_API_KEY no está definida en Netlify. En el navegador se cargará sin clave y la app no podrá llamar a la API de Gemini.');
  process.exitCode = 1;
} else {
  console.log('config.js generado. La clave queda visible en el cliente: restringe su uso por HTTP referrer (dominio .netlify.app) en la consola de la API de Gemini.');
}