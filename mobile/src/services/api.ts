// src/services/api.ts — ARIA Complete API Service v2
import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';

export const BASE_URL = __DEV__ ? 'http://10.0.2.2:8080' : 'https://api.aria-emergency.com';
const WS_URL = __DEV__ ? 'ws://10.0.2.2:8080' : 'wss://api.aria-emergency.com';

let _token: string | null = null;
let _deviceId: string | null = null;

export async function initApi() {
  _token    = await AsyncStorage.getItem('token');
  _deviceId = await AsyncStorage.getItem('deviceId');
}
export function setToken(t: string)    { _token = t;    AsyncStorage.setItem('token', t); }
export function setDeviceId(d: string) { _deviceId = d; AsyncStorage.setItem('deviceId', d); }
export function clearAuth()            { _token = null; _deviceId = null; AsyncStorage.multiRemove(['token','deviceId','user']); }
export const getToken    = () => _token;
export const getDeviceId = () => _deviceId;

const http = axios.create({ baseURL: BASE_URL, timeout: 15000 });
http.interceptors.request.use(cfg => {
  if (_token) cfg.headers.Authorization = `Bearer ${_token}`;
  return cfg;
});
http.interceptors.response.use(r => r, err => {
  if (err.response?.status === 401) clearAuth();
  return Promise.reject(err);
});

export interface AuthResponse { token: string; role: string; name: string; email: string; deviceId: string; }
export interface Emergency    { id: number; message: string; riskLevel: string; status: string; latitude?: number; longitude?: number; aiAction?: string; createdAt: string; reportedByDeviceId?: string; fallDetected?: boolean; }
export interface EmergencyResponse { emergencyId: number; riskLevel: string; nearbyUsersAlerted: number; aiAction?: string; aiAnalysis?: string; }
export interface DisasterAlert { id: number; type: string; title: string; description: string; severity: string; epicenterLat?: number; epicenterLon?: number; radiusKm?: number; magnitude?: number; issuedAt: string; }
export interface NearbyUser   { id: number; name: string; role: string; latitude?: number; longitude?: number; specialization?: string; vehicleId?: string; isOnDuty?: boolean; isAvailable?: boolean; }
export interface MedicalConsultation { id: number; roomId: string; status: string; doctor?: NearbyUser; patient?: NearbyUser; requestedAt: string; notes?: string; prescription?: string; }
export interface ChatMessage  { id: number; role: string; content: string; createdAt: string; }
export interface ChatResponse { reply: string; sessionId: string; isEmergency: boolean; timestamp: string; }

export const Auth = {
  register:  (b: any) => http.post<AuthResponse>('/api/auth/register', b).then(r => r.data),
  login:     (email: string, password: string) => http.post<AuthResponse>('/api/auth/login', { email, password }).then(r => r.data),
  guest:     (deviceId?: string) => http.post<AuthResponse>(`/api/auth/guest${deviceId ? `?deviceId=${deviceId}` : ''}`).then(r => r.data),
};

export const EmergencyApi = {
  report:    (b: any) => http.post<EmergencyResponse>('/api/emergency/report', b).then(r => r.data),
  getAll:    (page = 0) => http.get<Emergency[]>(`/api/emergency/all?page=${page}&size=20`).then(r => r.data),
  getActive: ()          => http.get<Emergency[]>('/api/emergency/active').then(r => r.data),
  resolve:   (id: number) => http.patch<Emergency>(`/api/emergency/${id}/resolve`).then(r => r.data),
  search:    (params: Record<string,any>) => http.get<Emergency[]>('/api/emergency/search', { params }).then(r => r.data),
};

export const LocationApi = {
  update:           (lat: number, lon: number, deviceId: string) => http.post('/api/location/update', { latitude: lat, longitude: lon, deviceId }),
  shareWithPolice:  (lat: number, lon: number, emergencyId?: number) => http.post('/api/location/share', { latitude: lat, longitude: lon, targetRole: 'POLICE', emergencyId }).then(r => r.data),
  shareWithAmbulance:(lat: number, lon: number, emergencyId?: number) => http.post('/api/location/share', { latitude: lat, longitude: lon, targetRole: 'AMBULANCE', emergencyId }).then(r => r.data),
  stopSharing:      (sessionId: string) => http.delete(`/api/location/share/${sessionId}`),
  getHistory:       (deviceId: string) => http.get(`/api/location/history/${deviceId}`).then(r => r.data),
};

export const DisasterApi = {
  getActive:  ()                             => http.get<DisasterAlert[]>('/api/disasters/active').then(r => r.data),
  getNearby:  (lat: number, lon: number)     => http.get<DisasterAlert[]>(`/api/disasters/nearby?lat=${lat}&lon=${lon}`).then(r => r.data),
};

export const MedicalApi = {
  nearbyDoctors:    (lat: number, lon: number) => http.get<NearbyUser[]>(`/api/medical/doctors/nearby?lat=${lat}&lon=${lon}`).then(r => r.data),
  nearbyAmbulances: (lat: number, lon: number) => http.get<NearbyUser[]>(`/api/medical/ambulances/nearby?lat=${lat}&lon=${lon}`).then(r => r.data),
  nearbyPolice:     (lat: number, lon: number) => http.get<NearbyUser[]>(`/api/medical/police/nearby?lat=${lat}&lon=${lon}`).then(r => r.data),
  requestConsult:   (doctorId: number, emergencyId?: number) => http.post<MedicalConsultation>('/api/medical/consultation', { doctorId, emergencyId }).then(r => r.data),
  acceptConsult:    (id: number) => http.patch<MedicalConsultation>(`/api/medical/consultation/${id}/accept`).then(r => r.data),
  endConsult:       (id: number, notes: string, prescription?: string) => http.patch<MedicalConsultation>(`/api/medical/consultation/${id}/end`, { notes, prescription }).then(r => r.data),
  pending:          () => http.get<MedicalConsultation[]>('/api/medical/consultation/pending').then(r => r.data),
};

export const DispatchApi = {
  setDuty:  (onDuty: boolean, isAvailable: boolean) => http.patch('/api/dispatch/duty', { onDuty, isAvailable }).then(r => r.data),
  respond:  (emergencyId: number, etaMinutes: string, victimDeviceId?: string) => http.post('/api/dispatch/respond', { emergencyId, etaMinutes, victimDeviceId }).then(r => r.data),
  doctors:    () => http.get<NearbyUser[]>('/api/dispatch/doctors').then(r => r.data),
  police:     () => http.get<NearbyUser[]>('/api/dispatch/police').then(r => r.data),
  ambulances: () => http.get<NearbyUser[]>('/api/dispatch/ambulances').then(r => r.data),
};

export const ChatApi = {
  send:       (message: string, sessionId: string) => http.post<ChatResponse>('/api/chat/message', { message, sessionId }).then(r => r.data),
  getHistory: (sessionId: string)                   => http.get<ChatMessage[]>(`/api/chat/history/${sessionId}`).then(r => r.data),
  clear:      (sessionId: string)                   => http.delete(`/api/chat/history/${sessionId}`),
};

export const VoiceApi = {
  setKeyword:    (keyword: string, hint?: string) => http.post('/api/voice/keyword', { keyword, hint }).then(r => r.data),
  getStatus:     ()                               => http.get('/api/voice/keyword/status').then(r => r.data),
  disable:       ()                               => http.post('/api/voice/keyword/disable').then(r => r.data),
  silentTrigger: (lat: number, lon: number, deviceId: string) => http.post('/api/voice/trigger', { latitude: lat, longitude: lon, deviceId }).then(r => r.data),
};

export const AiApi = {
  reportSignal: (signal: string, lat: number, lon: number, deviceId: string) => http.post('/api/ai/detect/signal', { signal, latitude: lat, longitude: lon, deviceId }).then(r => r.data),
};

export const AlertsApi = {
  getMine:     () => http.get('/api/alerts/mine').then(r => r.data),
  getUnread:   () => http.get('/api/alerts/mine/unread-count').then(r => r.data),
  acknowledge: (id: number) => http.patch(`/api/alerts/${id}/acknowledge`).then(r => r.data),
};

export const AnalyticsApi = {
  timeline: () => http.get('/api/analytics/timeline').then(r => r.data),
  risk:     () => http.get('/api/analytics/risk').then(r => r.data),
  trend:    () => http.get('/api/analytics/trend').then(r => r.data),
};

export class AriaWebSocket {
  private ws: WebSocket | null = null;
  private listeners = new Map<string, (d: any) => void>();
  private reconnectTimer: any = null;

  constructor(private deviceId: string) {}

  connect() {
    try {
      this.ws = new WebSocket(`${WS_URL}/ws/websocket`);
      this.ws.onopen = () => {
        [`/topic/alerts/${this.deviceId}`, '/topic/disaster/broadcast', '/topic/emergency/broadcast']
          .forEach(dest => this.ws?.send(`SUBSCRIBE\ndestination:${dest}\nid:${Date.now()}\n\n\0`));
      };
      this.ws.onmessage = evt => {
        try { const m = JSON.parse(evt.data); this.listeners.forEach(cb => cb(m)); } catch {}
      };
      this.ws.onclose = () => { this.reconnectTimer = setTimeout(() => this.connect(), 5000); };
    } catch {}
  }

  on(key: string, cb: (d: any) => void) { this.listeners.set(key, cb); }
  off(key: string)                       { this.listeners.delete(key); }
  disconnect() {
    if (this.reconnectTimer) clearTimeout(this.reconnectTimer);
    this.ws?.close(); this.ws = null;
  }
}

// ── Custom Maps API (OpenStreetMap, no key needed) ─────────────────────────────
export const MapsApi = {
  tiles:       () => http.get('/api/maps/tiles').then(r => r.data),
  reverse:     (lat: number, lon: number) => http.get(`/api/maps/reverse?lat=${lat}&lon=${lon}`).then(r => r.data),
  geocode:     (q: string) => http.get(`/api/maps/geocode?q=${encodeURIComponent(q)}`).then(r => r.data),
  nearby:      (lat: number, lon: number, type: string, radius = 5000) =>
    http.get(`/api/maps/places/nearby?lat=${lat}&lon=${lon}&type=${type}&radius=${radius}`).then(r => r.data),
  route:       (fromLat: number, fromLon: number, toLat: number, toLon: number) =>
    http.get(`/api/maps/route?fromLat=${fromLat}&fromLon=${fromLon}&toLat=${toLat}&toLon=${toLon}`).then(r => r.data),
};

// ── Custom WebRTC Signaling (no Twilio/Agora needed) ──────────────────────────
export const WebRtcApi = {
  createRoom:     (callerDeviceId: string, calleeDeviceId: string, callType = 'VIDEO') =>
    http.post('/api/webrtc/room', { callerDeviceId, calleeDeviceId, callType }).then(r => r.data),
  getRoom:        (roomId: string) => http.get(`/api/webrtc/room/${roomId}`).then(r => r.data),
  sendOffer:      (roomId: string, sdp: string, deviceId: string) =>
    http.post('/api/webrtc/signal/offer', { roomId, sdp, deviceId }).then(r => r.data),
  sendAnswer:     (roomId: string, sdp: string, deviceId: string) =>
    http.post('/api/webrtc/signal/answer', { roomId, sdp, deviceId }).then(r => r.data),
  sendIce:        (roomId: string, candidate: string, sdpMid: string, sdpMLineIndex: number, deviceId: string) =>
    http.post('/api/webrtc/signal/ice', { roomId, candidate, sdpMid, sdpMLineIndex, deviceId }).then(r => r.data),
  endCall:        (roomId: string, deviceId: string) =>
    http.post('/api/webrtc/end', { roomId, deviceId }).then(r => r.data),
};

// ── Custom Push Notifications (no Firebase needed) ────────────────────────────
export const PushApi = {
  sendToDevice:    (deviceId: string, title: string, body: string, type = 'NOTIFICATION') =>
    http.post('/api/push/send', { deviceId, title, body, type }).then(r => r.data),
  getPending:      (deviceId: string) =>
    http.get(`/api/push/pending/${deviceId}`).then(r => r.data),
  subscribeSSE:    (deviceId: string, onMessage: (d: any) => void) => {
    const es = new EventSource(`${BASE_URL}/api/push/subscribe/${deviceId}`);
    es.onmessage = e => { try { onMessage(JSON.parse(e.data)); } catch {} };
    return es;
  },
};

// TILE_URL for OpenStreetMap (use directly in map components)
export const OSM_TILE_URL = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
export const OSM_ATTRIBUTION = '© OpenStreetMap contributors';
