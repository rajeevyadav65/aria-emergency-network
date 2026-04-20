# Contributing to ARIA Emergency Network

Thank you for your interest in contributing! This document explains how to get started.

---

## Project Structure

```
emergency-system/
├── backend/                    Spring Boot application
│   ├── src/main/java/          Production code
│   │   └── com/emergency/system/
│   │       ├── config/         Security, WebSocket, rate limiting, events
│   │       ├── controller/     REST endpoints
│   │       ├── dto/            Request/response objects
│   │       ├── model/          JPA entities
│   │       ├── repository/     Spring Data JPA repositories
│   │       └── service/        Business logic
│   ├── src/main/resources/
│   │   ├── application*.properties
│   │   └── static/             Frontend (index.html, sw.js, manifest.json)
│   └── src/test/               Unit + integration tests
├── .github/workflows/ci.yml    GitHub Actions CI
├── docker-compose.yml
├── nginx.conf
├── schema.sql                  PostgreSQL schema
└── ARIA-API.postman_collection.json
```

---

## Setting Up Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- (Optional) Docker + Docker Compose
- (Optional) Anthropic API key

### Run

```bash
cd backend
export ANTHROPIC_API_KEY=sk-ant-your-key    # optional
mvn spring-boot:run
```

Open **http://localhost:8080** — the UI loads directly.

Demo credentials: `alice@demo.com / demo123` or `admin@aria.com / admin123`

---

## Running Tests

```bash
cd backend
mvn test -Dspring.profiles.active=test
```

Tests use an isolated H2 in-memory DB (`application-test.properties`) — no external deps needed.

### Test coverage
| Layer | Test class | Tests |
|-------|-----------|-------|
| Controller | `AuthControllerTest` | 5 |
| Controller | `EmergencyControllerTest` | 4 |
| Controller | `ChatControllerTest` | 4 |
| Controller | `UserProfileControllerTest` | 5 |
| Controller | `AdminControllerTest` | 5 |
| Controller | `EmergencySearchControllerTest` | 6 |
| Controller | `AlertControllerTest` | 3 |
| Service | `AiAnalysisServiceTest` | 6 |
| Service | `EmergencyServiceTest` | 5 |
| Service | `EmergencySearchServiceTest` | 6 |
| Service | `LocationServiceTest` | 4 |
| Repository | `UserRepositoryTest` | 5 |
| Repository | `NotificationLogRepositoryTest` | 4 |
| Repository | `LocationHistoryRepositoryTest` | 4 |

---

## Adding a New Feature

### 1. New REST endpoint

1. Add DTO to `EmergencyDTOs.java`
2. Create or update repository if needed
3. Add business logic to an existing or new service
4. Add controller method with `@Valid` on request body
5. Update `SecurityConfig` whitelist if the endpoint should be public
6. Add integration test in `src/test/java/.../controller/`
7. Add request to `ARIA-API.postman_collection.json`

### 2. New entity

1. Create entity class in `model/` with `@Entity`, Lombok annotations
2. Create repository in `repository/` extending `JpaRepository`
3. Add to `schema.sql` for PostgreSQL
4. Seed demo data in `DataSeeder` if helpful

### 3. Frontend page

1. Write a React function component in `index.html`
2. Add nav item to the `nav` array in `App`
3. Add page entry to the `pages` map in `App`

---

## Coding Standards

- **Java**: Lombok for boilerplate, `@Slf4j` for logging, `@Transactional` on service methods that write
- **DTOs**: Use `@Valid` + constraint annotations for all request bodies
- **Tests**: Use `@ActiveProfiles("test")`, never mock the real H2 DB
- **Commits**: Conventional commits — `feat:`, `fix:`, `test:`, `docs:`

---

## Environment Variables

| Variable | Required | Description |
|---|---|---|
| `ANTHROPIC_API_KEY` | No | Claude API key. Without it, rule-based fallback is used |
| `APP_JWT_SECRET` | Prod only | 32+ char JWT signing key |
| `SPRING_PROFILES_ACTIVE` | Docker | Set to `docker` or `prod` |

---

## Reporting Issues

Open a GitHub issue with:
- Steps to reproduce
- Expected vs actual behaviour
- Spring Boot logs (set `logging.level.com.emergency=DEBUG`)
