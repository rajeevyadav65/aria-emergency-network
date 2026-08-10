package com.emergency.system.service;

import com.emergency.system.model.LocationHistory;
import com.emergency.system.model.User;
import com.emergency.system.repository.LocationHistoryRepository;
import com.emergency.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Live Location Sharing Service
 *
 * Enables victims to share real-time GPS with:
 * - Assigned ambulance driver
 * - Responding police officer
 * - On-duty doctor (for consultation)
 *
 * Architecture:
 * - In-memory map: deviceId → Set<responderUserId> (active shares)
 * - On every location update, push to all subscribed responders via WebSocket
 * - Responders subscribe to /topic/location/{deviceId}
 *
 * WebSocket channels:
 * - Victim shares to:   /topic/location/{victimDeviceId}
 * - Responder receives: /topic/location/{victimDeviceId}
 * - Responder pushes:   /topic/responder/{victimDeviceId} (ETA, responder position)
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class LiveLocationSharingService {

    private final LocationHistoryRepository locationHistoryRepo;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /** deviceId → set of responder userIds who are tracking this device */
    private final Map<String, Set<Long>> activeShares = new ConcurrentHashMap<>();

    /** responderUserId → deviceId they are tracking */
    private final Map<Long, String> responderTracking = new ConcurrentHashMap<>();

    // ── Share Management ──────────────────────────────────────────────────────

    /**
     * Victim explicitly shares location with a specific responder.
     * Called when: ambulance dispatched, police responding, doctor consultation starts.
     */
    public Map<String, Object> startSharing(String deviceId, Long responderUserId) {
        activeShares.computeIfAbsent(deviceId, k -> ConcurrentHashMap.newKeySet())
                .add(responderUserId);
        responderTracking.put(responderUserId, deviceId);

        // Notify the responder they now have tracking access
        messagingTemplate.convertAndSend(
                "/topic/alerts/" + getResponderDeviceId(responderUserId),
                Map.<String, Object>of("type", "LOCATION_SHARE_STARTED",
                        "victimDeviceId", deviceId,
                        "message", "You now have live location access"));

        log.info("[LOCATION] Share started: device={} → responder={}", deviceId, responderUserId);

        // FIX: Added <String, Object>
        return Map.<String, Object>of("sharing", true, "deviceId", deviceId,
                "responderUserId", responderUserId);
    }

    /**
     * Stop sharing with a specific responder.
     */
    public void stopSharing(String deviceId, Long responderUserId) {
        Set<Long> shares = activeShares.get(deviceId);
        if (shares != null) {
            shares.remove(responderUserId);
            if (shares.isEmpty()) activeShares.remove(deviceId);
        }
        responderTracking.remove(responderUserId);

        messagingTemplate.convertAndSend(
                "/topic/alerts/" + getResponderDeviceId(responderUserId),
                Map.<String, Object>of("type", "LOCATION_SHARE_STOPPED", "victimDeviceId", deviceId));

        log.info("[LOCATION] Share stopped: device={} → responder={}", deviceId, responderUserId);
    }

    /**
     * Stop all shares for a device (e.g. emergency resolved).
     */
    public void stopAllShares(String deviceId) {
        Set<Long> responders = activeShares.remove(deviceId);
        if (responders != null) {
            responders.forEach(rid -> responderTracking.remove(rid));
        }
    }

    /**
     * Returns the list of responder userIds tracking this device.
     */
    public List<Long> getActiveResponders(String deviceId) {
        return new ArrayList<>(activeShares.getOrDefault(deviceId, Collections.emptySet()));
    }

    // ── Location Broadcasting ──────────────────────────────────────────────────

    /**
     * Called on every GPS update from the victim's device.
     * Saves history and broadcasts to all active responders.
     *
     * @param deviceId  the victim's device
     * @param latitude  GPS lat
     * @param longitude GPS lon
     * @param accuracy  GPS accuracy in metres
     * @param speed     speed in m/s (optional)
     */
    @Async
    public void broadcastLocation(String deviceId, double latitude, double longitude,
                                  Float accuracy, Float speed) {

        // Save to history
        saveLocationHistory(deviceId, latitude, longitude);

        // Build the location payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("type",      "LOCATION_UPDATE");
        payload.put("deviceId",  deviceId);
        payload.put("latitude",  latitude);
        payload.put("longitude", longitude);
        payload.put("accuracy",  accuracy);
        payload.put("speed",     speed);
        payload.put("timestamp", System.currentTimeMillis());

        // Broadcast to general location topic (police/ambulance dashboards)
        messagingTemplate.convertAndSend("/topic/location/" + deviceId, payload);

        // Also push to each responder's personal alert channel
        Set<Long> responders = activeShares.getOrDefault(deviceId, Collections.emptySet());
        for (Long responderId : responders) {
            String responderDevice = getResponderDeviceId(responderId);
            if (responderDevice != null) {
                messagingTemplate.convertAndSend("/topic/alerts/" + responderDevice, payload);
            }
        }

        if (!responders.isEmpty()) {
            log.debug("[LOCATION] Broadcast {},{} → {} responders", latitude, longitude, responders.size());
        }
    }

    /**
     * Responder pushes their own location so victim can see ETA.
     */
    @Async
    public void broadcastResponderLocation(Long responderUserId, double latitude,
                                           double longitude, String etaMinutes) {
        String victimDeviceId = responderTracking.get(responderUserId);
        if (victimDeviceId == null) return;

        userRepository.findById(responderUserId).ifPresent(responder ->
                messagingTemplate.convertAndSend("/topic/responder/" + victimDeviceId,
                        // FIX: Added <String, Object>
                        Map.<String, Object>of("type",        "RESPONDER_LOCATION",
                                "responderId", responderUserId,
                                "role",        responder.getRole().name(),
                                "name",        responder.getName() != null ? responder.getName() : "Responder",
                                "latitude",    latitude,
                                "longitude",   longitude,
                                "etaMinutes",  etaMinutes,
                                "timestamp",   System.currentTimeMillis()))
        );
    }

    // ── Victim Tracking (for responders) ─────────────────────────────────────

    /**
     * Responder queries the latest location of a victim.
     */
    public Map<String, Object> getLatestLocation(String victimDeviceId) {
        return locationHistoryRepo
                .findTopByDeviceIdOrderByRecordedAtDesc(victimDeviceId)
                // FIX: Added <String, Object> directly to Map.of instead of casting
                .map(h -> Map.<String, Object>of(
                        "deviceId",   victimDeviceId,
                        "latitude",   h.getLatitude(),
                        "longitude",  h.getLongitude(),
                        "recordedAt", h.getRecordedAt().toString()
                ))
                // FIX: Added <String, Object>
                .orElse(Map.<String, Object>of("error", "No location data available"));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void saveLocationHistory(String deviceId, double lat, double lon) {
        userRepository.findByDeviceId(deviceId).ifPresent(user -> {
            user.setLatitude(lat);
            user.setLongitude(lon);
            user.setLocationUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
        });

        locationHistoryRepo.save(LocationHistory.builder()
                .deviceId(deviceId).latitude(lat).longitude(lon)
                .recordedAt(LocalDateTime.now()).build());
    }

    private String getResponderDeviceId(Long userId) {
        return userRepository.findById(userId)
                .map(User::getDeviceId)
                .orElse(null);
    }

    public Map<String, Object> getShareStatus(String deviceId) {
        List<Long> responders = getActiveResponders(deviceId);
        // FIX: Added <String, Object>
        return Map.<String, Object>of(
                "deviceId",      deviceId,
                "sharingWith",   responders.size(),
                "responderIds",  responders,
                "isBeingTracked", !responders.isEmpty()
        );
    }
}