# ARIA Emergency Network — Complete System Architecture

## 1. High-Level Architecture Diagram

```
┌──────────────────────────────────────────────────────────────────────────┐
│                          ARIA ECOSYSTEM v2.0                             │
│                                                                          │
│  ┌─────────────────────────┐    ONLINE    ┌──────────────────────────┐  │
│  │    ANDROID CLIENT        │◄────REST────►│  SPRING BOOT BACKEND     │  │
│  │                          │◄──WebSocket──│  Port 8080               │  │
│  │  ┌─────────────────┐    │              │                          │  │
│  │  │CommunicationMgr │    │              │  Controllers (12)         │  │
│  │  │  autoSwitch()   │    │              │  Services (15)            │  │
│  │  └────┬────────────┘    │              │  Repositories (12)        │  │
│  │       │ online/offline  │              │  Models (10 entities)     │  │
│  │  ┌────▼──────┐ ┌──────┐ │              │                          │  │
│  │  │OnlineSvc  │ │Offline│ │   OFFLINE   │  Claude AI API            │  │
│  │  │REST/WS    │ │BT+P2P ├─┼────P2P──────│  USGS Earthquake API      │  │
│  │  └───────────┘ └──────┘ │  ~100-200m  │  Google Maps/Places       │  │
│  │                          │              │  WebRTC (video calls)     │  │
│  │  ┌─────────────────┐    │              └──────────────────────────┘  │
│  │  │   AI ENGINE      │    │                           │                │
│  │  │ ML Kit (offline) │    │                    ┌──────▼──────┐        │
│  │  │ Fall Detector    │    │                    │  H2 / PG DB  │        │
│  │  │ Voice Trigger    │    │                    │  10 tables   │        │
│  │  └─────────────────┘    │                    └─────────────┘        │
│  │                          │                                           │
│  │  ┌─────────────────┐    │  ┌────────────────────────────────────┐  │
│  │  │  Room SQLite DB  │    │  │       WEB FRONTEND (React SPA)     │  │
│  │  │  Offline queue   │    │  │  Dashboard · SOS · Chat · Map      │  │
│  │  │  SyncManager     │    │  │  Search · Alerts · Notifications   │  │
│  │  └─────────────────┘    │  │  Settings · Admin · API Docs       │  │
│  └─────────────────────────┘  └────────────────────────────────────┘  │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Mode Auto-Switch Logic

```
Every 5 seconds:
IF hasInternet() AND wasOffline:
    → ONLINE mode
    → webSocket.connect()
    → syncManager.flushQueue()   ← push all offline data to server
    → bluetooth.stopScan()
ELSE IF !hasInternet() AND wasOnline:
    → OFFLINE mode
    → webSocket.disconnect()
    → bluetooth.startDiscovery()
    → wifiDirect.discoverPeers()

sendEmergency(report):
    IF ONLINE  → POST /api/emergency/report
    IF OFFLINE → enqueue(report) + BLE broadcast + WiFi Direct broadcast
```

---

## 3. AI Detection Pipeline

```
Camera Frame (every N frames)
         │
         ▼
   ┌─────────────┐     ┌─────────────────────┐
   │  ML Kit     │────►│  FaceDetectionEngine │
   │  Face API   │     │  - eye openness      │
   └─────────────┘     │  - head euler angle  │
                        │  - smile probability │
   ┌─────────────┐     └──────────┬──────────┘
   │ Accelerom.  │                │ PANIC_FACE/UNCONSCIOUS
   │ Sensor      │     ┌──────────▼──────────┐
   └──────┬──────┘     │  FallDetectionSvc   │
          │            │  impact >25m/s²      │
          └───────────►│  + stillness check   │
                        └──────────┬──────────┘
                                   │ FALL_DETECTED
                                   ▼
                          conf > threshold?
                          ┌──────┴──────┐
                        YES             NO
                          │             │
                          ▼             ▼
                    AUTO-FIRE     Cloud Validate
                    EMERGENCY     (Claude Vision)
                    /api/         /api/ai/detect/frame
                    emergency/
                    report
```

---

## 4. Voice Trigger Flow

```
Settings: user sets keyword "HELP123"
    → hash = BCrypt("help123")
    → stored in EncryptedSharedPreferences
    → POST /api/voice/keyword { hash } → server stores backup

BACKGROUND:
SpeechRecognizer (on-device, offline-first, EXTRA_PREFER_OFFLINE=true)
    → continuous listening
    → recognized: "hello there"  → hash ≠ stored hash → ignore
    → recognized: "help123"      → hash MATCHES!

SILENT SOS (no UI alert, no sound):
    → POST /api/voice/trigger { lat, lon, deviceId }
    → Server: EmergencyRequest { message="SILENT SOS", riskLevel=HIGH }
    → Alerts all nearby users via WebSocket
    → Server logs trigger event
```

---

## 5. User Roles & Flows

```
NORMAL USER:
  → Register/Login → Send SOS → Track own location
  → View nearby alerts → AI Chat (ARIA) → Set voice keyword

DOCTOR:
  → Login with license number → Set ON_DUTY status
  → Receive consultation requests via WebSocket
  → Accept/reject → Video call via WebRTC room
  → Write notes + prescription

POLICE:
  → Login with badge number → Set ON_DUTY
  → Receive HIGH risk alerts → Track victim live on map
  → Dispatch response → POST /api/dispatch/respond

AMBULANCE:
  → Login with vehicle ID → Set AVAILABLE
  → Receive nearest emergency → Navigate → Update ETA
  → POST /api/dispatch/respond { etaMinutes: "8" }

ADMIN:
  → Full dashboard → Manage users/roles
  → Create manual disaster alerts
  → View system health (/actuator/health)
  → Access Swagger UI (/swagger-ui.html)
```

---

## 6. Offline P2P Communication

```
BLUETOOTH (BLE GATT — ~100m):
  Advertiser (emergency device):
    → builds AdvertiseData with ARIA_SERVICE_UUID + payload
    → startAdvertising() — other devices see it immediately

  Scanner (nearby devices):
    → ScanFilter on ARIA_SERVICE_UUID
    → onScanResult() → parse payload → show alert
    → if payload too large: connectGatt() → readCharacteristic()

WIFI DIRECT (~200m, higher bandwidth):
  Group Owner: starts TCP server on port 8899
  Client: connects to group owner, sends JSON emergency payload
  Use case: sharing camera frames / larger payloads
  
SYNC-ON-RECONNECT:
  All offline data stored in Room SQLite with SyncItem type
  On internet restore: POST /api/sync/batch { deviceId, items: [...] }
  Server returns { idMappings: { localId → serverId } }
  Device updates local DB with server IDs
```

---

## 7. Disaster Alert System

```
USGS Earthquake Feed (polled every 5 min):
  https://earthquake.usgs.gov/fdsnws/event/1/query
  → M4.0+ earthquakes in last hour
  → New entries → create DisasterAlert entity
  → broadcastDisasterAlert():
      - /topic/disaster/broadcast  (all connected WS clients)
      - /topic/alerts/{deviceId}   (each user in affected area)

Manual Admin Alert:
  POST /api/disasters { type, title, epicenterLat, epicenterLon, radiusKm }
  → same broadcast pipeline

Alert Types: EARTHQUAKE, FLOOD, FIRE, CYCLONE, TSUNAMI, LANDSLIDE,
             INDUSTRIAL_ACCIDENT, TERRORIST_ATTACK, PANDEMIC
Severity:    LOW, MEDIUM, HIGH, CRITICAL
```

---

## 8. WebRTC Video Consultation Flow

```
1. Patient: POST /api/medical/consultation { doctorId }
   → Server creates MedicalConsultation { roomId: "room-abc123", status: REQUESTED }
   → WS push to doctor: { type: CONSULTATION_REQUEST, roomId, patientName }

2. Doctor: PATCH /api/medical/consultation/{id}/accept
   → status = ACTIVE, startedAt = now
   → WS push to patient: { type: CONSULTATION_ACCEPTED, roomId }

3. Both sides: connect to WebRTC room using roomId
   (WebRTC signaling server: wss://signal.aria-emergency.com)
   → peer-to-peer video/audio

4. Doctor: PATCH /api/medical/consultation/{id}/end
   → { notes, prescription }
   → status = COMPLETED, endedAt = now
```

---

## 9. Complete API Reference

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/auth/register | — | Register (any role) |
| POST | /api/auth/login | — | Login → JWT |
| POST | /api/auth/guest | — | Guest session |
| POST | /api/emergency/report | — | Report emergency |
| GET | /api/emergency/search | — | Multi-filter search |
| GET | /api/emergency/active | 🔑 | Active emergencies |
| PATCH | /api/emergency/{id}/resolve | 🔑 | Resolve |
| POST | /api/ai/detect/frame | — | Camera frame analysis |
| POST | /api/ai/detect/signal | — | On-device signal report |
| POST | /api/voice/keyword | 🔑 | Set voice trigger |
| POST | /api/voice/trigger | 🔑 | Silent SOS |
| GET | /api/medical/doctors/nearby | — | Find doctors |
| GET | /api/medical/ambulances/nearby | — | Find ambulances |
| POST | /api/medical/consultation | 🔑 | Request consultation |
| PATCH | /api/dispatch/duty | 🔑 | Toggle on-duty |
| POST | /api/dispatch/respond | 🔑 | Accept dispatch |
| GET | /api/disasters/active | — | Active disasters |
| POST | /api/disasters | 🔑 ADMIN | Create disaster alert |
| POST | /api/sync/batch | — | Offline sync |
| GET | /api/alerts/mine | 🔑 | My alerts |
| GET | /api/admin/stats | 🔑 ADMIN | Full statistics |
| GET | /actuator/health | — | Health check |
| GET | /swagger-ui.html | — | API documentation |

---

## 10. Running the Full Stack

```bash
# Backend
cd backend
export ANTHROPIC_API_KEY=sk-ant-...   # optional
mvn spring-boot:run

# URLs:
# App:     http://localhost:8080
# Swagger: http://localhost:8080/swagger-ui.html
# H2:      http://localhost:8080/h2-console
# Health:  http://localhost:8080/actuator/health

# Demo accounts (seeded automatically):
# USER:       alice@demo.com / demo123
# DOCTOR:     dr.sharma@aria.com / doctor123
# POLICE:     officer.singh@aria.com / police123
# AMBULANCE:  ambulance1@aria.com / amb123
# ADMIN:      admin@aria.com / admin123

# Docker
docker-compose up --build

# Tests
mvn test -Dspring.profiles.active=test
```
