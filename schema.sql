-- ARIA Emergency Network — PostgreSQL Schema
-- Use this when deploying to PostgreSQL (production).
-- For H2 dev, Spring auto-generates the schema via hibernate.ddl-auto=create-drop.

-- ── Users ─────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(255),
    email                VARCHAR(255) UNIQUE,
    password             VARCHAR(255),
    device_id            VARCHAR(255) NOT NULL UNIQUE,
    latitude             DOUBLE PRECISION,
    longitude            DOUBLE PRECISION,
    role                 VARCHAR(20)  NOT NULL DEFAULT 'GUEST'
                             CHECK (role IN ('GUEST','USER','ADMIN')),
    created_at           TIMESTAMP    NOT NULL DEFAULT NOW(),
    location_updated_at  TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_users_device_id ON users(device_id);
CREATE INDEX IF NOT EXISTS idx_users_email     ON users(email);
-- Haversine nearby-user query needs lat/lon
CREATE INDEX IF NOT EXISTS idx_users_location  ON users(latitude, longitude)
    WHERE latitude IS NOT NULL AND longitude IS NOT NULL;

-- ── Emergencies ───────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS emergencies (
    id                      BIGSERIAL PRIMARY KEY,
    message                 TEXT,
    latitude                DOUBLE PRECISION,
    longitude               DOUBLE PRECISION,
    fall_detected           BOOLEAN,
    movement                VARCHAR(50),
    user_response           VARCHAR(50),
    reported_by_device_id   VARCHAR(255),
    risk_level              VARCHAR(20) CHECK (risk_level IN ('LOW','MEDIUM','HIGH')),
    ai_action               VARCHAR(500),
    status                  VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                                CHECK (status IN ('PENDING','ACTIVE','RESOLVED','FALSE_ALARM')),
    created_at              TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved_at             TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_emergencies_status     ON emergencies(status);
CREATE INDEX IF NOT EXISTS idx_emergencies_risk       ON emergencies(risk_level);
CREATE INDEX IF NOT EXISTS idx_emergencies_device     ON emergencies(reported_by_device_id);
CREATE INDEX IF NOT EXISTS idx_emergencies_created    ON emergencies(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_emergencies_location   ON emergencies(latitude, longitude)
    WHERE latitude IS NOT NULL;

-- ── Alerts ────────────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS alerts (
    id               BIGSERIAL PRIMARY KEY,
    emergency_id     BIGINT       NOT NULL REFERENCES emergencies(id) ON DELETE CASCADE,
    user_id          BIGINT       NOT NULL REFERENCES users(id)       ON DELETE CASCADE,
    message          TEXT,
    distance_meters  DOUBLE PRECISION,
    status           VARCHAR(20) NOT NULL DEFAULT 'SENT'
                         CHECK (status IN ('SENT','ACKNOWLEDGED','RESPONDED')),
    sent_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    UNIQUE (emergency_id, user_id)
);
CREATE INDEX IF NOT EXISTS idx_alerts_emergency ON alerts(emergency_id);
CREATE INDEX IF NOT EXISTS idx_alerts_user      ON alerts(user_id);

-- ── Chat Messages ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS chat_messages (
    id          BIGSERIAL PRIMARY KEY,
    session_id  VARCHAR(255) NOT NULL,
    role        VARCHAR(20)  NOT NULL CHECK (role IN ('USER','ASSISTANT')),
    content     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_chat_session ON chat_messages(session_id, created_at);

-- ── Notification Logs ─────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS notification_logs (
    id                    BIGSERIAL PRIMARY KEY,
    type                  VARCHAR(50) NOT NULL,
    emergency_id          BIGINT,
    recipient_device_id   VARCHAR(255),
    channel               VARCHAR(50),
    message               TEXT,
    delivered             BOOLEAN NOT NULL DEFAULT FALSE,
    failure_reason        TEXT,
    sent_at               TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_notif_emergency ON notification_logs(emergency_id);
CREATE INDEX IF NOT EXISTS idx_notif_sent_at   ON notification_logs(sent_at DESC);
CREATE INDEX IF NOT EXISTS idx_notif_delivered ON notification_logs(delivered);

-- ── Location History ──────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS location_history (
    id               BIGSERIAL PRIMARY KEY,
    device_id        VARCHAR(255) NOT NULL,
    latitude         DOUBLE PRECISION NOT NULL,
    longitude        DOUBLE PRECISION NOT NULL,
    accuracy_meters  DOUBLE PRECISION,
    speed_mps        DOUBLE PRECISION,
    recorded_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_loc_hist_device  ON location_history(device_id, recorded_at DESC);
CREATE INDEX IF NOT EXISTS idx_loc_hist_time    ON location_history(recorded_at DESC);

-- ── Cleanup function (call from cron or pg_cron) ──────────────────────────────
-- DELETE FROM location_history WHERE recorded_at < NOW() - INTERVAL '24 hours';

-- ── Disaster Alerts ───────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS disaster_alerts (
    id               BIGSERIAL PRIMARY KEY,
    type             VARCHAR(50) NOT NULL,
    title            VARCHAR(500) NOT NULL,
    description      TEXT,
    source           VARCHAR(100),
    external_id      VARCHAR(255) UNIQUE,
    epicenter_lat    DOUBLE PRECISION,
    epicenter_lon    DOUBLE PRECISION,
    radius_km        DOUBLE PRECISION,
    magnitude        DOUBLE PRECISION,
    severity         VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    alert_status     VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    users_notified   INTEGER DEFAULT 0,
    issued_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    expires_at       TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_disaster_status  ON disaster_alerts(alert_status, issued_at DESC);
CREATE INDEX IF NOT EXISTS idx_disaster_type    ON disaster_alerts(type);

-- ── Medical Consultations ─────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS medical_consultations (
    id              BIGSERIAL PRIMARY KEY,
    patient_id      BIGINT REFERENCES users(id),
    doctor_id       BIGINT REFERENCES users(id),
    emergency_id    BIGINT REFERENCES emergencies(id),
    room_id         VARCHAR(100) UNIQUE,
    session_token   VARCHAR(255),
    type            VARCHAR(20) NOT NULL DEFAULT 'VIDEO',
    status          VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
    notes           TEXT,
    prescription    VARCHAR(500),
    requested_at    TIMESTAMP NOT NULL DEFAULT NOW(),
    started_at      TIMESTAMP,
    ended_at        TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_consult_patient ON medical_consultations(patient_id, requested_at DESC);
CREATE INDEX IF NOT EXISTS idx_consult_doctor  ON medical_consultations(doctor_id, status);

-- ── Offline Sync Queue ────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS offline_sync_queue (
    id                  BIGSERIAL PRIMARY KEY,
    device_id           VARCHAR(255) NOT NULL,
    type                VARCHAR(50) NOT NULL,
    payload             TEXT NOT NULL,
    local_id            VARCHAR(100) NOT NULL UNIQUE,
    sync_status         VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    server_assigned_id  VARCHAR(100),
    error_message       TEXT,
    retry_count         INTEGER DEFAULT 0,
    created_offline_at  TIMESTAMP NOT NULL DEFAULT NOW(),
    synced_at           TIMESTAMP
);
CREATE INDEX IF NOT EXISTS idx_sync_device  ON offline_sync_queue(device_id, sync_status);
CREATE INDEX IF NOT EXISTS idx_sync_status  ON offline_sync_queue(sync_status, retry_count);

-- ── Voice Keywords ────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS voice_keywords (
    id                BIGSERIAL PRIMARY KEY,
    user_id           BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    keyword_hash      VARCHAR(255) NOT NULL,
    hint              VARCHAR(100),
    active            BOOLEAN NOT NULL DEFAULT TRUE,
    created_at        TIMESTAMP NOT NULL DEFAULT NOW(),
    last_triggered_at TIMESTAMP,
    trigger_count     INTEGER DEFAULT 0
);

-- ── Additional indexes for analytics queries ──────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_emergencies_created_at    ON emergencies(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_emergencies_risk_status   ON emergencies(risk_level, status);
CREATE INDEX IF NOT EXISTS idx_emergencies_lat_lon       ON emergencies(latitude, longitude) WHERE latitude IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_disaster_alerts_status    ON disaster_alerts(alert_status, issued_at DESC);
CREATE INDEX IF NOT EXISTS idx_medical_consultations_status ON medical_consultations(status, requested_at DESC);
CREATE INDEX IF NOT EXISTS idx_offline_sync_queue_device ON offline_sync_queue(device_id, sync_status);
CREATE INDEX IF NOT EXISTS idx_voice_keywords_user       ON voice_keywords(user_id, active);

-- ── Analytics view for dashboard ─────────────────────────────────────────────
CREATE OR REPLACE VIEW emergency_stats AS
SELECT
    COUNT(*)                                                        AS total_emergencies,
    COUNT(*) FILTER (WHERE status = 'ACTIVE')                       AS active,
    COUNT(*) FILTER (WHERE status = 'RESOLVED')                     AS resolved,
    COUNT(*) FILTER (WHERE risk_level = 'HIGH')                     AS high_risk,
    COUNT(*) FILTER (WHERE status = 'FALSE_ALARM')                  AS false_alarms,
    ROUND(COUNT(*) FILTER (WHERE status = 'RESOLVED')::numeric /
          NULLIF(COUNT(*), 0) * 100, 1)                             AS resolution_rate_pct,
    ROUND(COUNT(*) FILTER (WHERE status = 'FALSE_ALARM')::numeric /
          NULLIF(COUNT(*), 0) * 100, 1)                             AS false_alarm_rate_pct
FROM emergencies;
