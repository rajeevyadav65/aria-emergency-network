# Changelog

All notable changes to the ARIA Emergency Network are documented here.
Format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/).

---

## [1.0.0] — 2025-04-09 — Initial Release (Full Stack)

### Added

#### Core Features
- **Emergency reporting** — `POST /api/emergency/report` (public)
  - AI risk classification via Anthropic Claude API (HIGH/MEDIUM/LOW)
  - Rule-based fallback when no API key configured
  - Context-aware false-alarm filtering
  - Nearby user detection via Haversine formula (configurable radius)
  - Real-time WebSocket alert dispatch to nearby devices

- **AI Chat (ARIA)** — `POST /api/chat/message`
  - Conversational emergency assistant powered by Claude
  - Per-session history stored in database
  - Emergency intent detection with `isEmergency` flag
  - Voice input (Web Speech API) + TTS replies (SpeechSynthesis)

- **Authentication**
  - JWT-based stateless auth (JJWT 0.11.5)
  - Register, Login, Guest session endpoints
  - BCrypt password hashing
  - IP-based rate limiting (30 req/min) on auth/report/chat endpoints

- **Location Services**
  - GPS position update endpoint (public)
  - Location history tracking (last 24h trail)
  - Scheduled hourly pruning of old history records
  - Haversine-based nearby user query (JPQL, H2-compatible)

- **WebSocket (STOMP/SockJS)**
  - Per-device alert channel: `/topic/alerts/{deviceId}`
  - High-risk broadcast: `/topic/emergency/broadcast`
  - `@Async` dispatch to avoid blocking request threads

- **Admin Panel**
  - Full stats: resolution rate, false-alarm rate, notification delivery
  - User management with role update
  - Custom Actuator health indicator with emergency counts

- **Search** — `GET /api/emergency/search`
  - Filter by keyword, risk level, status, device ID, time window, geo radius
  - All filters combinable, newest-first default sort

- **User Profile**
  - Name update, password change (with current-pw verification)
  - Location trail endpoint (last 24h)

- **Alert Management**
  - Retrieve personal alerts, acknowledge read status, unread count

#### Spring Application Events
- `EmergencyCreatedEvent`, `EmergencyResolvedEvent`, `HighRiskEmergencyEvent`
- Async, decoupled — ready for SMS/email integration

#### Input Validation
- `@NotBlank`, `@Email`, `@Size`, `@NotNull`, `@DecimalMin/Max` on all DTOs
- `MethodArgumentNotValidException` handler returns field-level error map

#### Frontend (React SPA, 10 pages)
- Dashboard with Chart.js timeline + resolution/false-alarm rate progress bars
- SOS Report with animated button + GPS auto-fill
- ARIA Chat with voice input, TTS, quick-message buttons
- Live Feed (WebSocket alerts)
- Live Map (Leaflet.js + OpenStreetMap, dark theme, emergency pins, location trail polyline)
- My Notifications (personal alert inbox with acknowledge)
- Search (multi-filter)
- Admin Panel with doughnut chart
- Settings (profile + password)
- API Reference

#### Infrastructure
- Dockerfile (multi-stage, non-root user, health check)
- `docker-compose.yml` + `docker-compose.override.yml`
- `nginx.conf` (WebSocket upgrade, gzip, security headers)
- `schema.sql` (full PostgreSQL schema, 6 tables, 14 indexes)
- `application.properties` (dev), `-prod`, `-docker`, `-test`
- GitHub Actions CI (build + test + Docker + frontend validation)
- Postman collection (20 requests with test scripts)
- PWA manifest + Service Worker (offline shell, push notifications)
- JaCoCo code coverage (60% minimum line coverage gate)

#### Testing (18 test files, ~70 tests)
- Controller integration tests: Auth, Emergency, Chat, UserProfile, Admin, Search, Alert
- Service unit tests: AiAnalysis, EmergencyService (false-alarm logic), EmergencySearch, Location (Haversine)
- Repository JPA slice tests: UserRepository (Haversine), NotificationLog, LocationHistory
- Config unit tests: JwtUtil, RateLimitFilter, AriaHealthIndicator, GlobalExceptionHandler

### Fixed (from original project)
- `Alert.java` and `Emergency.java` were swapped — class names matched wrong files
- `UserService.java` had duplicate `@Slf4j` import causing compile error
- `pom.xml` had `spring-ai-client-chat` M4 milestone + `spring-context 7.0.6` version conflict
- `EmergencyRepository` missing `findByStatus()` used in controller
- `Emergency` model missing 6 fields used in service (`fallDetected`, `movement`, etc.)
- `Alert` model missing `AlertStatus` enum + `status` field
- `SecutityConfig` typo + missing `WebSocketConfig`
- No CORS config — frontend calls all blocked
- No error handling — raw Java stack traces returned to client
- No demo data — blank UI on first run
- `AdminController` referenced in security config but never created

---

## [Unreleased] — Planned

- PostgreSQL production migration guide
- Twilio SMS integration for HIGH risk emergencies
- SendGrid email alerts
- React Native mobile app
- Interactive Leaflet cluster map for multiple emergencies
- OpenAPI / Swagger UI documentation
- Redis session store for horizontal scaling
- Multi-language support (Hindi)

---

## [2.0.0] — 2025-04-11 — Intelligent Emergency Ecosystem

### Added

#### System Modes (Online + Offline)
- `CommunicationManager.kt` — auto-switches between ONLINE and OFFLINE every 5s
- `BluetoothService.kt` — BLE GATT advertising (~100m range), emergency broadcast
- `WiFiDirectService.kt` — WiFi Direct P2P TCP socket (~200m), large payloads
- `SyncManager.kt` — offline queue → server sync on reconnect
- `POST /api/sync/batch` — accepts offline batch, returns localId→serverId mappings

#### Multi-Role System
- 4 new roles: `DOCTOR`, `POLICE`, `AMBULANCE` (in addition to USER/ADMIN/GUEST)
- `RegisterRequest` updated with `role`, `licenseNumber`, `specialization`, `vehicleId`
- `UserRepository` — new Haversine queries filtered by role
- `PATCH /api/dispatch/duty` — toggle on/off duty
- `GET /api/dispatch/doctors`, `/ambulances`, `/police` — on-duty lists
- `POST /api/dispatch/respond` — responder accepts emergency, notifies victim with ETA

#### AI Detection
- `AiDetectionService.java` — Claude Vision API for fall/panic/unconscious detection
- `AiDetectionManager.kt` — on-device ML Kit + accelerometer fall detector
- `FaceDetectionEngine.kt` — ML Kit face analysis (eye openness, head angle)
- `FallDetectionService.kt` — impact spike + post-stillness algorithm
- `POST /api/ai/detect/frame` — camera frame analysis
- `POST /api/ai/detect/signal` — lightweight on-device signal report

#### Voice Trigger (Privacy-First)
- `VoiceKeyword` entity — BCrypt-hashed keyword, never plaintext
- `VoiceKeywordService.java` — set, verify, disable, get status
- `VoiceKeywordDetector.kt` — continuous background recognition, on-device hash compare
- `POST /api/voice/keyword` — set secret keyword
- `POST /api/voice/trigger` — silent SOS when keyword detected

#### Medical Services
- `MedicalConsultation` entity — patient, doctor, roomId, status, notes
- `MedicalService.java` — request consultation, nearby doctors/ambulances/police
- `MedicalController.java` — full consultation lifecycle endpoints
- WebRTC room creation for video consultations

#### Disaster Alerts
- `DisasterAlert` entity — type, severity, epicenter, radius, source
- `DisasterAlertService.java` — polls USGS earthquake API every 5 minutes (free, no key)
- `POST /api/disasters` (admin) — manual disaster creation
- Broadcasts to all users in affected geographic radius via WebSocket

#### New Database Entities (4)
- `disaster_alerts` — with geo-distance query for affected users
- `medical_consultations` — consultation lifecycle
- `offline_sync_queue` — idempotent batch sync
- `voice_keywords` — BCrypt-hashed secret trigger

#### Infrastructure
- `OpenApiConfig.java` — Swagger UI at `/swagger-ui.html` with JWT Bearer auth
- `RequestLoggingFilter.java` — method/path/status/duration logging
- JaCoCo 60% line coverage gate in CI
- 5 new test files (DisasterAlertService, MedicalService, VoiceKeywordService + others)
- Postman collection extended to 35 requests across 12 groups
- `schema.sql` extended with 4 new tables

#### Frontend (12 pages now)
- **🌍 Disasters** — live disaster alerts, severity colors, USGS data
- **🏥 Nearby Help** — find doctors/ambulances/police within 10km
- **DutyToggle** — DOCTOR/POLICE/AMBULANCE can toggle on/off duty from dashboard
- Registration form extended with role, license, specialization, vehicle ID
- Demo credentials updated with Doctor/Police/Ambulance accounts

#### Android Additions
- `AriaDatabase.kt` — Room SQLite: offline emergencies, alerts, chat cache, location
- `LiveLocationService.kt` — Google Fused Location Provider, real-time tracking

---

## [1.1.0] — 2025-04-12 — Analytics, Offline Sync, Role Dispatch

### Added

#### Analytics Engine (new)
- `EmergencyAnalyticsService` + `EmergencyAnalyticsController`
- 6 new public REST endpoints under `/api/analytics/`
- Timeline: hourly emergency counts for the last 24 hours
- Risk level breakdown (HIGH/MEDIUM/LOW/FALSE_ALARM)
- Status breakdown (ACTIVE/RESOLVED/FALSE_ALARM)
- 7-day trend with week-over-week percentage change
- Geographic hotspot clustering (0.01° grid ~1km²)
- Users by role (USER/DOCTOR/POLICE/AMBULANCE/ADMIN)

#### Dual-Mode System (Online + Offline)
- `CommunicationManager` — auto-switches every 5s based on connectivity
- `OnlineService` — REST/WebSocket client for Android
- `OfflineService` — orchestrates BLE + WiFi Direct in parallel
- `EmergencyBackgroundService` — Android foreground service (fall/voice/BLE)
- `OfflineEntities.kt` — Room DB: OfflineEmergency, OfflineLocation, ReceivedAlert, CachedDisasterAlert
- `SyncManager` — Android-side queue with ConnectivityManager callback

#### Multi-Role System
- 6 roles: GUEST, USER, DOCTOR, POLICE, AMBULANCE, ADMIN
- Registration accepts `role`, `licenseNumber`, `specialization`, `vehicleId`
- `RoleDispatchController` — duty toggle, on-duty lists, dispatch respond
- `MedicalController` — consultation lifecycle + nearby services
- `VoiceKeywordController` — BCrypt keyword set/verify + silent trigger
- `AiDetectionController` — camera frame + on-device signal endpoints

#### Disaster Alerts
- `DisasterAlertService` — USGS earthquake feed (M4.0+, polled 5 min)
- Manual admin alerts via `POST /api/disasters`
- WebSocket broadcast to all users in affected radius
- 10 disaster types: EARTHQUAKE, FLOOD, FIRE, CYCLONE, TSUNAMI, etc.

#### AI Detection Pipeline
- `AiDetectionService` — Claude Vision + on-device signal fallback
- `AiDetectionManager.kt` — ML Kit face (panic/unconscious) + accelerometer fall
- `VoiceKeywordDetector.kt` — background SpeechRecognizer, offline-first, BCrypt hash

#### Infrastructure
- `nginx.conf` — rate limiting zones: auth (10/min), report (5/min), api (60/min)
- OpenAPI / Swagger UI — `springdoc-openapi 2.3.0`, JWT auth scheme
- `WebSocketConfig` — STOMP auth interceptor (JWT validation on CONNECT)
- `AppConfig` — production CORS with `app.cors.allowed-origins` property
- `docker-compose.override.yml` — local dev with remote debug port 5005
- `AriaApplication.kt` + `AppModule.kt` — Hilt DI
- `EmergencyViewModel.kt` — Android MVVM with StateFlow

### Tests Added (20 new test files)
- `JwtUtilTest`, `RateLimitFilterTest`, `AriaHealthIndicatorTest`
- `GlobalExceptionHandlerTest`, `EmergencyEventListenerTest`
- `AiDetectionControllerTest`, `VoiceKeywordControllerTest`
- `DisasterAlertControllerTest`, `MedicalControllerTest`
- `SyncControllerTest`, `RoleDispatchControllerTest`
- `EmergencyExportControllerTest`, `LocationUserControllerTest`
- `EmergencyAnalyticsControllerTest`
- `AiChatServiceTest`, `UserServiceTest`, `DisasterAlertServiceTest`
- `MedicalServiceTest`, `VoiceKeywordServiceTest`, `OfflineSyncServiceTest`
- `EmergencyAnalyticsServiceTest`

## [1.1.0] — 2025-04-12 — Analytics + Consultation + Full Test Coverage

### Added

#### Analytics System
- `EmergencyAnalyticsService` — 6 data methods: timeline (24h), risk breakdown, status, 7-day trend with week-over-week %, geographic hotspot clustering, users by role
- `EmergencyAnalyticsController` — 6 public endpoints at `/api/analytics/*`
- **React Analytics page** — custom SVG bar charts, donut charts, legend lists, trend indicators
- PostgreSQL analytics view (`emergency_stats`)
- 7 additional performance indexes on key columns

#### Doctor Consultation
- **React Consultation page** — browse nearby on-duty doctors, request video call, WebRTC room display

#### Test Coverage (7 new test files, ~45 new tests)
- `AiDetectionServiceTest` — 6 tests: signal routing, emergency trigger, disabled mode, null signal
- `EmergencyAnalyticsServiceTest` — 6 tests: timeline labels, hotspot clustering, role breakdown
- `EmergencyAnalyticsControllerTest` — 7 integration tests
- `VoiceKeywordControllerTest` — 5 tests
- `AiDetectionControllerTest` — 5 tests
- `EmergencyExportControllerTest` — 5 tests
- `RoleDispatchControllerTest` — 6 tests

#### Android
- `BootReceiver.kt` — restarts monitoring foreground service after device reboot
- `AppModule.kt` — Hilt DI singleton bindings
- `EmergencyViewModel.kt` — MVVM ViewModel with StateFlow, offline emergency saving, location tracking
- `AriaApplication.kt` — @HiltAndroidApp entry point
- `AndroidManifest.xml` — all 24 permissions + foreground service types

#### Infrastructure
- `WebSocketConfig` — JWT auth on STOMP CONNECT frame (authenticated users get their token recognized)
- `AppConfig` — production-safe CORS with `app.cors.allowed-origins` property
- `SecurityConfig` — dispatch GET endpoints made public; analytics endpoints public
- **Postman collection** — extended to 68 requests across 21 folders

### Fixed
- `StatsService` — duplicate field declarations removed
- `WebSocketConfig` — `@Lazy @Autowired` instead of broken `@RequiredArgsConstructor` + `@Lazy final` combo
- `SecurityConfig` — missing `@Lazy` import added; dispatch GET endpoints incorrectly required auth
