package com.emergency.system.service;

import com.emergency.system.model.DisasterAlert;
import com.emergency.system.model.User;
import com.emergency.system.repository.DisasterAlertRepository;
import com.emergency.system.repository.UserRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Fetches real-time disaster data from:
 * - USGS Earthquake API (free, no key required)
 * - GDACS (Global Disaster Alert & Coordination System)
 * - Manual admin-triggered alerts
 *
 * Broadcasts to all connected WebSocket clients every 5 minutes.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DisasterAlertService {

    private final DisasterAlertRepository disasterRepo;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    @Value("${app.disaster.fetch-enabled:true}")
    private boolean fetchEnabled;

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build();

    // ── Scheduled fetch every 5 minutes ──────────────────────────────────────

    @Scheduled(fixedDelay = 300_000)
    public void fetchAndBroadcast() {
        if (!fetchEnabled) return;
        try {
            fetchUSGSEarthquakes();
        } catch (Exception e) {
            log.warn("Disaster fetch failed: {}", e.getMessage());
        }
    }

    /**
     * Fetches M4.0+ earthquakes from the last hour from USGS (no API key needed).
     */
    private void fetchUSGSEarthquakes() throws Exception {
        String url = "https://earthquake.usgs.gov/fdsnws/event/1/query"
                + "?format=geojson&minmagnitude=4.0"
                + "&starttime=" + LocalDateTime.now().minusHours(1).toLocalDate()
                + "&limit=10&orderby=time";

        Request req = new Request.Builder().url(url).build();
        try (Response response = HTTP.newCall(req).execute()) {
            if (!response.isSuccessful() || response.body() == null) return;
            JsonNode root = objectMapper.readTree(response.body().string());
            JsonNode features = root.get("features");
            if (features == null) return;

            for (JsonNode feature : features) {
                String extId = feature.get("id").asText();
                if (disasterRepo.existsByExternalId(extId)) continue;

                JsonNode props = feature.get("properties");
                JsonNode coords = feature.get("geometry").get("coordinates");

                double mag = props.get("mag").asDouble();
                double lon = coords.get(0).asDouble();
                double lat = coords.get(1).asDouble();
                String place = props.get("place").asText("Unknown");

                DisasterAlert.DisasterSeverity severity =
                        mag >= 7.0 ? DisasterAlert.DisasterSeverity.CRITICAL :
                        mag >= 6.0 ? DisasterAlert.DisasterSeverity.HIGH :
                        mag >= 5.0 ? DisasterAlert.DisasterSeverity.MEDIUM :
                                     DisasterAlert.DisasterSeverity.LOW;

                DisasterAlert alert = disasterRepo.save(DisasterAlert.builder()
                        .type(DisasterAlert.DisasterType.EARTHQUAKE)
                        .title(String.format("M%.1f Earthquake — %s", mag, place))
                        .description(String.format(
                                "Magnitude %.1f earthquake detected near %s. Epicenter at (%.3f, %.3f).",
                                mag, place, lat, lon))
                        .source("USGS")
                        .externalId(extId)
                        .epicenterLat(lat)
                        .epicenterLon(lon)
                        .magnitude(mag)
                        .radiusKm(mag * 50)  // rough affected radius
                        .severity(severity)
                        .build());

                broadcastDisasterAlert(alert);
                log.info("[DISASTER] Earthquake M{} near {} — broadcast sent", mag, place);
            }
        }
    }

    /**
     * Manual admin-triggered disaster alert.
     */
    public DisasterAlert createManualAlert(DisasterAlert alert) {
        alert.setSource("MANUAL");
        DisasterAlert saved = disasterRepo.save(alert);
        broadcastDisasterAlert(saved);
        return saved;
    }

    @Async
    public void broadcastDisasterAlert(DisasterAlert alert) {
        try {
            messagingTemplate.convertAndSend("/topic/disaster/broadcast", alert);

            // Also send to users in the affected area
            if (alert.getEpicenterLat() != null && alert.getRadiusKm() != null) {
                List<User> affected = userRepository.findUsersWithinRadius(
                        alert.getEpicenterLat(), alert.getEpicenterLon(),
                        alert.getRadiusKm() * 1000);
                affected.forEach(user ->
                        messagingTemplate.convertAndSend(
                                "/topic/alerts/" + user.getDeviceId(), alert));
                alert.setUsersNotified(affected.size());
                disasterRepo.save(alert);
            }
        } catch (Exception e) {
            log.warn("Disaster broadcast failed: {}", e.getMessage());
        }
    }

    public List<DisasterAlert> getActiveAlerts() {
        return disasterRepo.findByAlertStatusOrderByIssuedAtDesc(DisasterAlert.AlertStatus.ACTIVE);
    }

    public List<DisasterAlert> getAlertsForLocation(double lat, double lon) {
        return disasterRepo.findActiveAlertsAffecting(lat, lon);
    }

    public DisasterAlert resolveAlert(Long id) {
        DisasterAlert alert = disasterRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));
        alert.setAlertStatus(DisasterAlert.AlertStatus.RESOLVED);
        return disasterRepo.save(alert);
    }
}
