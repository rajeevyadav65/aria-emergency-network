// ARIA Emergency Network — Service Worker
// Provides offline shell + caches static assets

const CACHE_NAME = 'aria-v1';
const STATIC_ASSETS = ['/', '/index.html', '/manifest.json'];

// Install: cache shell
self.addEventListener('install', event => {
  event.waitUntil(
    caches.open(CACHE_NAME).then(cache => cache.addAll(STATIC_ASSETS))
  );
  self.skipWaiting();
});

// Activate: remove old caches
self.addEventListener('activate', event => {
  event.waitUntil(
    caches.keys().then(keys =>
      Promise.all(keys.filter(k => k !== CACHE_NAME).map(k => caches.delete(k)))
    )
  );
  self.clients.claim();
});

// Fetch: network-first for API, cache-first for static
self.addEventListener('fetch', event => {
  const url = new URL(event.request.url);

  // API and WebSocket — always network
  if (url.pathname.startsWith('/api/') || url.pathname.startsWith('/ws')) {
    event.respondWith(fetch(event.request).catch(() =>
      new Response(JSON.stringify({ error: 'Offline — please check your connection' }), {
        status: 503, headers: { 'Content-Type': 'application/json' }
      })
    ));
    return;
  }

  // Static assets — cache first, network fallback
  event.respondWith(
    caches.match(event.request).then(cached =>
      cached || fetch(event.request).then(response => {
        const clone = response.clone();
        caches.open(CACHE_NAME).then(cache => cache.put(event.request, clone));
        return response;
      })
    ).catch(() => caches.match('/index.html'))
  );
});

// Push notifications
self.addEventListener('push', event => {
  const data = event.data?.json() || { title: 'ARIA Alert', body: 'Emergency nearby' };
  event.waitUntil(
    self.registration.showNotification(data.title || '🚨 ARIA Emergency Alert', {
      body: data.body || data.message || 'An emergency has been reported near you.',
      icon: '/manifest.json',
      badge: '/manifest.json',
      tag: 'emergency-alert',
      requireInteraction: data.riskLevel === 'HIGH',
      vibrate: [200, 100, 200, 100, 200],
      data: { url: '/', emergencyId: data.emergencyId }
    })
  );
});

self.addEventListener('notificationclick', event => {
  event.notification.close();
  event.waitUntil(clients.openWindow(event.notification.data?.url || '/'));
});
