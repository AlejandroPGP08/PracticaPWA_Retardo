// Nombre de la cache
const CACHE_NAME = 'voting-app-v1';
// Archivos a cachear
const urlsToCache = [
  '/',
  '/index.html',
  '/styles.css',
  '/app.js',
  '/manifest.json'
];

self.addEventListener('install', (event) => {
  // Espera a que la instalación termine
  event.waitUntil(
    caches.open(CACHE_NAME)
      // Cachea todos los archivos
      .then((cache) => cache.addAll(urlsToCache))
  );
});

self.addEventListener('fetch', (event) => {
  // Intercepta todas las peticiones fetch
  event.respondWith( 
    caches.match(event.request) // Busca en cache primero
      .then((response) => {
        if (response) {
          return response; // Retorna desde cache si existe
        }
        return fetch(event.request); // Si no, hace petición normal
      }
    )
  );
});