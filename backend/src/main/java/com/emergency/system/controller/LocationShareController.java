package com.emergency.system.controller;

import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Live location sharing endpoints.
 * FIXED: Path changed from /api/location to /api/location-tracking to avoid conflict with LiveLocationController
 */
@RestController
@RequestMapping("/api/location-tracking") // <-- FIX: Path changed to avoid conflict
@Tag(name = "Location", description = "GPS tracking and live location sharing")
@RequiredArgsConstructor
@Slf4j
public class LocationShareController {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    private final Map<String, LocationSession> activeSessions = new ConcurrentHashMap<>();

    public record LocationSession(
            String sessionId,
            String deviceId,
            String targetRole,
            double latitude,
            double longitude,
            Long emergencyId,
            LocalDateTime startedAt,
            boolean active
    ) {}

    @Operation(summary = "Update device GPS location")
    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateLocation(@RequestBody Map<String, Object> body) {
        String deviceId = (String) body.get("deviceId");
        double lat = body.containsKey("latitude")  ? ((Number) body.get("latitude")).doubleValue()  : 0.0;
        double lon = body.containsKey("longitude") ? ((Number) body.get("longitude")).doubleValue() : 0.0;

        if (deviceId == null) return ResponseEntity.badRequest().body(Map.of("error", "deviceId required"));

        userRepository.findByDeviceId(deviceId).ifPresent(user -> {
            user.setLatitude(lat);
            user.setLongitude(lon);
            user.setLocationUpdatedAt(LocalDateTime.now());
            userRepository.save(user);

            activeSessions.values().stream()
                    .filter(s -> s.deviceId().equals(deviceId) && s.active())
                    .forEach(session -> broadcastLocationUpdate(session, lat, lon));
        });

        return ResponseEntity.ok(Map.of("status", "updated"));
    }

    @Operation(summary = "Start sharing live location")
    @PostMapping("/share") // Ab ye clash nahi karega kyunki class path alag hai
    public ResponseEntity<Map<String, Object>> startSharing(
            @RequestBody Map<String, Object> body,
            @AuthenticationPrincipal UserDetails principal) {

        double lat = body.containsKey("latitude")  ? ((Number) body.get("latitude")).doubleValue()  : 0.0;
        double lon = body.containsKey("longitude") ? ((Number) body.get("longitude")).doubleValue() : 0.0;
        String targetRole  = (String) body.getOrDefault("targetRole", "POLICE");
        Long   emergencyId = body.containsKey("emergencyId") ?
                Long.valueOf(body.get("emergencyId").toString()) : null;

        String deviceId = "mobile-device";
        if (principal != null) {
            deviceId = userRepository.findByEmail(principal.getUsername())
                    .map(User::getDeviceId).orElse("mobile-device");
        } else if (body.containsKey("deviceId")) {
            deviceId = (String) body.get("deviceId");
        }

        String sessionId = "share-" + UUID.randomUUID().toString().substring(0, 8);
        LocationSession session = new LocationSession(
                sessionId, deviceId, targetRole, lat, lon, emergencyId,
                LocalDateTime.now(), true
        );
        activeSessions.put(sessionId, session);

        notifyRole(targetRole, lat, lon, deviceId, emergencyId, sessionId);

        return ResponseEntity.ok(Map.of(
                "sessionId", sessionId,
                "targetRole", targetRole,
                "message", "Location sharing started"
        ));
    }

    @Operation(summary = "Stop sharing")
    @DeleteMapping("/share/{sessionId}")
    public ResponseEntity<Map<String, String>> stopSharing(@PathVariable String sessionId) {
        activeSessions.remove(sessionId);
        return ResponseEntity.ok(Map.of("message", "Stopped", "sessionId", sessionId));
    }

    @Operation(summary = "Get active sessions")
    @GetMapping("/share/active")
    public ResponseEntity<List<LocationSession>> getActiveSessions(
            @AuthenticationPrincipal UserDetails principal) {
        if (principal == null) return ResponseEntity.ok(List.of());

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> {
                    List<LocationSession> sessions = activeSessions.values().stream()
                            .filter(s -> s.deviceId().equals(user.getDeviceId()) && s.active())
                            .toList();
                    return ResponseEntity.ok(sessions);
                })
                .orElse(ResponseEntity.ok(List.of()));
    }

    @Operation(summary = "Get history trail")
    @GetMapping("/history/{deviceId}")
    public ResponseEntity<List<Map<String, Object>>> getHistory(@PathVariable String deviceId) {
        return userRepository.findByDeviceId(deviceId)
                .map(user -> {
                    if (user.getLatitude() != null) {
                        return ResponseEntity.ok(List.of(Map.<String,Object>of(
                                "latitude", user.getLatitude(),
                                "longitude", user.getLongitude(),
                                "recordedAt", LocalDateTime.now().toString()
                        )));
                    }
                    return ResponseEntity.ok(List.<Map<String,Object>>of());
                })
                .orElse(ResponseEntity.ok(List.of()));
    }

    private void notifyRole(String role, double lat, double lon,
                            String sharerDeviceId, Long emergencyId, String sessionId) {
        try {
            User.UserRole userRole = User.UserRole.valueOf(role);
            List<User> targets = userRepository.findUsersWithinRadiusByRole(lat, lon, 15000, userRole);

            Map<String, Object> payload = Map.of(
                    "type", "LOCATION_SHARE_REQUEST",
                    "sharerDeviceId", sharerDeviceId,
                    "sessionId", sessionId
            );

            targets.forEach(target ->
                    messagingTemplate.convertAndSend("/topic/alerts/" + target.getDeviceId(), payload));
        } catch (Exception e) {
            log.warn("Notify error: {}", e.getMessage());
        }
    }

    private void broadcastLocationUpdate(LocationSession session, double lat, double lon) {
        Map<String, Object> update = Map.of(
                "type", "LOCATION_UPDATE",
                "sessionId", session.sessionId(),
                "latitude", lat,
                "longitude", lon
        );
        messagingTemplate.convertAndSend("/topic/dispatch/" + session.targetRole().toLowerCase(), update);
    }
}