package com.emergency.system.service;

import com.emergency.system.model.NotificationLog;
import com.emergency.system.model.User;
import com.emergency.system.repository.NotificationLogRepository;
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
 * Custom Push Notification Service — replaces Firebase FCM.
 *
 * Delivery channels (in order of preference):
 * 1. WebSocket (instant, if device is connected)
 * 2. SSE (Server-Sent Events, if WebSocket unavailable)
 * 3. Stored notification (picked up on next app open)
 *
 * All notifications are stored in NotificationLog for reliability.
 * No external service, no API key, works offline-first.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PushNotificationService {

    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationLogRepository notificationLogRepo;
    private final UserRepository userRepository;

    /** Connected SSE clients: deviceId → SseEmitter (managed separately) */
    private final Map<String, Object> connectedClients = new ConcurrentHashMap<>();

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Send a push notification to a specific device.
     * Tries WebSocket first, falls back to stored notification.
     */
    @Async
    public void sendToDevice(String deviceId, PushPayload payload) {
        try {
            // 1. Try WebSocket delivery (instant if connected)
            messagingTemplate.convertAndSend("/topic/alerts/" + deviceId, payload.toMap());
            log.debug("[PUSH] WS delivered to device {}: {}", deviceId, payload.title());

            // 2. Always log for reliability
            logNotification(deviceId, payload, "WEBSOCKET");
        } catch (Exception e) {
            log.warn("[PUSH] WS failed for {}: {} — storing for pickup", deviceId, e.getMessage());
            logNotification(deviceId, payload, "STORED");
        }
    }

    /**
     * Send to all devices of users with a specific role within a radius.
     */
    @Async
    public void sendToRole(User.UserRole role, double lat, double lon,
                           double radiusMeters, PushPayload payload) {
        List<User> targets = userRepository.findUsersWithinRadiusByRole(lat, lon, radiusMeters, role);
        targets.forEach(user -> sendToDevice(user.getDeviceId(), payload));
        log.info("[PUSH] Sent '{}' to {} {} users within {}m", payload.title(), targets.size(), role, (int)radiusMeters);
    }

    /**
     * Broadcast an emergency alert to all users in a radius (any role).
     */
    @Async
    public void broadcastEmergency(double lat, double lon, double radiusMeters,
                                   PushPayload payload) {
        List<User> targets = userRepository.findUsersWithinRadius(lat, lon, radiusMeters);

        // Broadcast to /topic/emergency/broadcast for all WS clients
        messagingTemplate.convertAndSend("/topic/emergency/broadcast", payload.toMap());

        // Also deliver to specific devices
        targets.forEach(user -> sendToDevice(user.getDeviceId(), payload));
        log.info("[PUSH] Emergency broadcast to {} users in {}m radius", targets.size(), (int)radiusMeters);
    }

    /**
     * Get undelivered notifications for a device (polled on app open).
     * Clears them after retrieval.
     */
    public List<Map<String, Object>> getPendingNotifications(String deviceId) {
        // In production: query NotificationLog for unread entries
        // Here we return a simple list from the log
        return notificationLogRepo.findAll().stream()
                .filter(n -> deviceId.equals(n.getDeviceId()) && !n.isDelivered())
                .map(n -> {
                    n.setDelivered(true);
                    n.setDeliveredAt(LocalDateTime.now());
                    notificationLogRepo.save(n);
                    return Map.<String, Object>of(
                            "id", n.getId(),
                            "title", n.getType(),
                            "body", n.getChannel(),
                            "timestamp", n.getSentAt().toString()
                    );
                })
                .toList();
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private void logNotification(String deviceId, PushPayload payload, String channel) {
        try {
            // FIX: Convert string to enum properly
            NotificationLog.NotificationType mappedType;
            try {
                mappedType = NotificationLog.NotificationType.valueOf(payload.type());
            } catch (Exception typeEx) {
                // Agar custom payload ho jo enum me na ho, toh default type set kardo
                mappedType = NotificationLog.NotificationType.SYSTEM_NOTIFICATION;
            }

            NotificationLog log = NotificationLog.builder()
                    .deviceId(deviceId)
                    .type(mappedType)
                    .channel(channel)
                    .delivered("WEBSOCKET".equals(channel))
                    .sentAt(LocalDateTime.now())
                    .build();
            notificationLogRepo.save(log);
        } catch (Exception e) {
            this.log.warn("[PUSH] Failed to log notification: {}", e.getMessage());
        }
    }

    // ── Push Payload record ───────────────────────────────────────────────────

    public record PushPayload(
            String type,        // EMERGENCY_ALERT, DISASTER_ALERT, CONSULTATION_REQUEST, etc.
            String title,
            String body,
            String riskLevel,
            Long   emergencyId,
            double latitude,
            double longitude,
            Map<String, Object> extra
    ) {
        public static PushPayload emergency(String title, String body, String risk,
                                            Long id, double lat, double lon) {
            return new PushPayload("EMERGENCY_ALERT", title, body, risk, id, lat, lon, Map.of());
        }

        public static PushPayload disaster(String title, String body) {
            return new PushPayload("HIGH_RISK_BROADCAST", title, body, "HIGH", null, 0, 0, Map.of());
        }

        public static PushPayload consultation(String doctorName, String roomId) {
            return new PushPayload("SYSTEM_NOTIFICATION",
                    "📞 Incoming consultation request",
                    "Dr. " + doctorName + " is requesting a video call",
                    "INFO", null, 0, 0, Map.of("roomId", roomId));
        }

        public static PushPayload locationShare(String sharer, String targetRole) {
            return new PushPayload("SYSTEM_NOTIFICATION",
                    "📍 Live location shared",
                    sharer + " is sharing their live location",
                    "INFO", null, 0, 0, Map.of("targetRole", targetRole));
        }

        public Map<String, Object> toMap() {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("type",        type);
            m.put("title",       title);
            m.put("body",        body);
            m.put("riskLevel",   riskLevel != null ? riskLevel : "");
            if (emergencyId != null) m.put("emergencyId", emergencyId);
            if (latitude  != 0) m.put("latitude",  latitude);
            if (longitude != 0) m.put("longitude", longitude);
            if (extra != null)  m.putAll(extra);
            m.put("timestamp", LocalDateTime.now().toString());
            return m;
        }
    }
}