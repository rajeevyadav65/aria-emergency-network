# ARIA Emergency Network v2.0

> **AI-powered emergency response platform** — works with internet (cloud) and without (peer-to-peer).

[![CI](https://github.com/your-org/aria-emergency/actions/workflows/ci.yml/badge.svg)](https://github.com/your-org/aria-emergency/actions)
[![Java](https://img.shields.io/badge/Java-17-blue)](https://openjdk.org/projects/jdk/17/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-green)](https://spring.io/projects/spring-boot)
[![Android](https://img.shields.io/badge/Android-8.0+-green)](https://developer.android.com)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## What Is ARIA?

ARIA (AI Response & Incident Assistant) is a full-stack emergency management system designed for both **urban** (internet-connected) and **rural** (offline P2P) scenarios.

### Key Capabilities

| Feature | Technology | Works Offline? |
|---|---|---|
| SOS Emergency Reporting | REST API | ✅ (BLE queue) |
| AI Triage (risk scoring) | Claude API + rule fallback | ✅ (fallback) |
| Real-Time Alerts | WebSocket / STOMP | ❌ Online only |
| Face/Fall Detection | ML Kit + Claude Vision | ✅ On-device |
| Voice Secret Trigger | Android SpeechRecognizer | ✅ On-device |
| Peer-to-Peer Alerts | Bluetooth LE (~100m) + WiFi Direct (~200m) | ✅ |
| Doctor Video Consultation | WebRTC | ❌ Online only |
| Disaster Alerts | USGS + GDACS APIs | ❌ Online only |
| GPS Live Tracking | Google Maps | ✅ (cached) |
| Data Sync | POST /api/sync/batch | Auto on reconnect |

---

## Quick Start

```bash
# 1. Clone
git clone https://github.com/your-org/aria-emergency.git
cd aria-emergency

# 2. (Optional) Set AI key
export ANTHROPIC_API_KEY=sk-ant-...

# 3. Run
cd backend && mvn spring-boot:run

# 4. Open
open http://localhost:8080
```

### Demo Credentials

| Role | Email | Password |
|---|---|---|
| Normal User | alice@demo.com | demo123 |
| Doctor | dr.sharma@aria.com | doctor123 |
| Police | officer.singh@aria.com | police123 |
| Ambulance | ambulance1@aria.com | amb123 |
| Admin | admin@aria.com | admin123 |

---

## Project Structure

```
aria-emergency/
├── backend/                    Spring Boot 3.2.5 + Java 17
│   ├── src/main/java/com/emergency/system/
│   │   ├── config/             12 config files (security, CORS, WS, events)
│   │   ├── controller/         15 controllers
│   │   ├── service/            15 services
│   │   ├── model/              10 JPA entities
│   │   ├── repository/         12 repositories
│   │   └── dto/                All request/response DTOs
│   └── src/test/               35 test files (~150 tests)
│
├── android/                    Android Kotlin (API 26+)
│   └── src/main/java/com/emergency/aria/
│       ├── ai/                 ML Kit face, accelerometer fall, voice keyword
│       ├── bluetooth/          BLE GATT advertising & scanning
│       ├── wifi/               WiFi Direct P2P
│       ├── service/            Online/Offline/Background service
│       ├── sync/               Offline → Online queue
│       ├── db/                 Room SQLite (4 entities)
│       └── ui/                 ViewModel + MainActivity
│
├── docs/ARCHITECTURE.md        Full system diagram + API reference
├── schema.sql                  PostgreSQL schema (10 tables)
├── ARIA-API.postman_collection.json  52 API requests
└── docker-compose.yml
```

---

## API Reference (Key Endpoints)

### Auth
```
POST /api/auth/register   { name, email, password, role, licenseNumber }
POST /api/auth/login      { email, password }
POST /api/auth/guest      ?deviceId=
```

### Emergency
```
POST  /api/emergency/report       Report emergency (triggers AI triage)
GET   /api/emergency/active        Active emergencies
GET   /api/emergency/search        Multi-filter: ?keyword=&riskLevel=&lat=&lon=&radiusKm=
PATCH /api/emergency/{id}/resolve  Resolve
GET   /api/export/emergencies.csv  CSV export (admin)
```

### AI Features
```
POST /api/ai/detect/frame    Analyze camera frame (Claude Vision)
POST /api/ai/detect/signal   Report on-device detection (FALL/PANIC_FACE)
POST /api/voice/keyword      Set secret SOS keyword
POST /api/voice/trigger      Silent SOS (keyword triggered)
```

### Medical & Dispatch
```
GET  /api/medical/doctors/nearby    ?lat=&lon=
GET  /api/medical/ambulances/nearby
POST /api/medical/consultation      Request video consultation
PATCH /api/dispatch/duty            Toggle on/off duty
POST  /api/dispatch/respond         Responder accepts emergency
```

### Disaster & Analytics
```
GET /api/disasters/active    Live disaster alerts (USGS + manual)
GET /api/analytics/timeline  Hourly emergency counts
GET /api/analytics/trend     7-day daily trend
GET /api/analytics/risk      By risk level (for charts)
GET /api/analytics/roles     User role distribution
```

### Offline Sync
```
POST /api/sync/batch    { deviceId, items: [{localId, type, payload}] }
GET  /api/sync/pending  ?deviceId=
```

### Infrastructure
```
GET /actuator/health     Health check + DB status + active emergencies
GET /swagger-ui.html     Interactive API documentation
GET /v3/api-docs         OpenAPI JSON spec
```

---

## System Modes

### Online (Internet available)
- REST API + WebSocket for real-time alerts
- Claude API for AI triage and face detection
- USGS earthquake polling every 5 minutes
- Google Maps live GPS tracking

### Offline (No internet)
- Bluetooth LE advertising — ~100m range, no pairing
- WiFi Direct P2P — ~200m range, supports image payloads
- Room SQLite for all data
- Auto-sync queue — flushes when internet restores

---

## Running Tests

```bash
cd backend
mvn test -Dspring.profiles.active=test
# 35 test files, ~150 tests, JaCoCo 60% minimum coverage
```

## Docker

```bash
docker-compose up --build
# Includes nginx reverse proxy with WebSocket upgrade
```

## Android Build

1. Open `android/` in Android Studio
2. Set `MAPS_API_KEY` in `local.properties`
3. Run on device (API 26+) — emulator supported via `10.0.2.2`

---

## License

MIT — see [LICENSE](LICENSE)
