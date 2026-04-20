package com.emergency.system.controller;

import com.emergency.system.service.PushNotificationService;
import com.emergency.system.service.PushNotificationService.PushPayload;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;

/**
 * Custom Push Notification API — no Firebase needed.
 *
 * POST /api/push/send              — send push to a device (admin/server use)
 * GET  /api/push/pending/{device}  — poll for undelivered notifications
 * GET  /api/push/subscribe/{device} — SSE stream (real-time, fallback to WS)
 */
@RestController
@RequestMapping("/api/push")
@Tag(name = "Custom Push Notifications", description = "Real-time push without Firebase — WebSocket + SSE")
@RequiredArgsConstructor
public class PushNotificationController {

    private final PushNotificationService pushService;
    private final Map<String, SseEmitter> sseClients = new ConcurrentHashMap<>();

    /** Send a push notification to a device (called internally or by admin) */
    @Operation(summary = "Send push to a specific device")
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendPush(@RequestBody Map<String, Object> body) {
        String deviceId  = (String) body.get("deviceId");
        String type      = (String) body.getOrDefault("type", "NOTIFICATION");
        String title     = (String) body.getOrDefault("title", "ARIA Alert");
        String message   = (String) body.getOrDefault("body", "");

        if (deviceId == null) return ResponseEntity.badRequest().body(Map.of("error", "deviceId required"));

        pushService.sendToDevice(deviceId, new PushPayload(type, title, message,
                null, null, 0, 0, Map.of()));

        return ResponseEntity.ok(Map.of("status", "sent", "deviceId", deviceId));
    }

    /** Get pending (undelivered) notifications for a device (app polls this on open) */
    @Operation(summary = "Poll pending notifications for a device")
    @GetMapping("/pending/{deviceId}")
    public ResponseEntity<List<Map<String, Object>>> getPending(@PathVariable String deviceId) {
        return ResponseEntity.ok(pushService.getPendingNotifications(deviceId));
    }

    /**
     * SSE stream — device subscribes and gets real-time events without polling.
     * This is the fallback when WebSocket isn't available (e.g. iOS background).
     *
     * Usage: EventSource('/api/push/subscribe/device-id')
     */
    @Operation(summary = "SSE stream for real-time push (WebSocket fallback)")
    @GetMapping(value = "/subscribe/{deviceId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable String deviceId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseClients.put(deviceId, emitter);

        emitter.onCompletion(() -> sseClients.remove(deviceId));
        emitter.onTimeout(()     -> sseClients.remove(deviceId));
        emitter.onError(e        -> sseClients.remove(deviceId));

        // Send connected confirmation
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("connected")
                        .data(Map.of("deviceId", deviceId, "status", "subscribed")));
            } catch (IOException e) {
                emitter.complete();
            }
        });

        return emitter;
    }

    /** Send event to a specific SSE subscriber */
    public void sendSseEvent(String deviceId, Object data) {
        SseEmitter emitter = sseClients.get(deviceId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event().name("alert").data(data));
            } catch (IOException e) {
                sseClients.remove(deviceId);
            }
        }
    }
}
