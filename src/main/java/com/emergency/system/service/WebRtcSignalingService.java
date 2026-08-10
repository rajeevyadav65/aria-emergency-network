package com.emergency.system.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom WebRTC Signaling Server — built into Spring Boot.
 * No Twilio, no Agora, no external service needed.
 *
 * Uses existing WebSocket/STOMP infrastructure.
 *
 * How it works:
 * 1. Caller creates a room via POST /api/webrtc/room
 * 2. Both peers connect to /topic/webrtc/{roomId}
 * 3. They exchange SDP offers/answers and ICE candidates via this service
 * 4. Once connected, video/audio streams peer-to-peer (no server relay)
 *
 * Rooms auto-expire after 30 minutes.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class WebRtcSignalingService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper objectMapper;

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    public record Room(
        String roomId,
        String callerDeviceId,
        String calleeDeviceId,
        String callType,          // VIDEO or VOICE
        RoomStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiresAt
    ) {}

    public enum RoomStatus { WAITING, CONNECTED, ENDED }

    // ── Room management ───────────────────────────────────────────────────────

    /** Create a new WebRTC room */
    public Map<String, Object> createRoom(String callerDeviceId,
                                           String calleeDeviceId,
                                           String callType) {
        String roomId = "room-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);

        Room room = new Room(
                roomId, callerDeviceId, calleeDeviceId,
                callType != null ? callType : "VIDEO",
                RoomStatus.WAITING,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30)
        );
        rooms.put(roomId, room);

        // Notify callee
        if (calleeDeviceId != null && !calleeDeviceId.isBlank()) {
            messagingTemplate.convertAndSend("/topic/alerts/" + calleeDeviceId, Map.of(
                    "type",     "INCOMING_CALL",
                    "roomId",   roomId,
                    "from",     callerDeviceId,
                    "callType", callType != null ? callType : "VIDEO",
                    "message",  "📞 Incoming " + (callType != null ? callType.toLowerCase() : "video") + " call"
            ));
        }

        log.info("[WEBRTC] Room created: {} | {} → {} | type={}", roomId, callerDeviceId, calleeDeviceId, callType);

        return Map.of(
                "roomId",       roomId,
                "callType",     callType != null ? callType : "VIDEO",
                "iceServers",   getIceServers(),
                "signalingUrl", "/topic/webrtc/" + roomId,
                "expiresAt",    room.expiresAt().toString()
        );
    }

    /** Signal: send SDP offer from caller to callee */
    public void sendOffer(String roomId, String sdpOffer, String fromDeviceId) {
        Room room = rooms.get(roomId);
        if (room == null) { log.warn("[WEBRTC] Room {} not found", roomId); return; }

        String target = room.callerDeviceId().equals(fromDeviceId)
                ? room.calleeDeviceId() : room.callerDeviceId();

        messagingTemplate.convertAndSend("/topic/webrtc/" + roomId, Map.of(
                "type",      "OFFER",
                "roomId",    roomId,
                "sdp",       sdpOffer,
                "from",      fromDeviceId,
                "timestamp", LocalDateTime.now().toString()
        ));

        // Also send directly to the target device
        if (target != null) {
            messagingTemplate.convertAndSend("/topic/alerts/" + target, Map.of(
                    "type", "WEBRTC_OFFER", "roomId", roomId, "sdp", sdpOffer
            ));
        }
        log.debug("[WEBRTC] Offer relayed in room {}", roomId);
    }

    /** Signal: send SDP answer from callee back to caller */
    public void sendAnswer(String roomId, String sdpAnswer, String fromDeviceId) {
        if (!rooms.containsKey(roomId)) return;

        messagingTemplate.convertAndSend("/topic/webrtc/" + roomId, Map.of(
                "type",      "ANSWER",
                "roomId",    roomId,
                "sdp",       sdpAnswer,
                "from",      fromDeviceId,
                "timestamp", LocalDateTime.now().toString()
        ));
        log.debug("[WEBRTC] Answer relayed in room {}", roomId);
    }

    /** Signal: relay ICE candidate between peers */
    public void sendIceCandidate(String roomId, String candidate,
                                  String sdpMid, int sdpMLineIndex,
                                  String fromDeviceId) {
        if (!rooms.containsKey(roomId)) return;

        messagingTemplate.convertAndSend("/topic/webrtc/" + roomId, Map.of(
                "type",          "ICE_CANDIDATE",
                "roomId",        roomId,
                "candidate",     candidate,
                "sdpMid",        sdpMid != null ? sdpMid : "",
                "sdpMLineIndex", sdpMLineIndex,
                "from",          fromDeviceId
        ));
    }

    /** End a call */
    public Map<String, Object> endCall(String roomId, String deviceId) {
        Room room = rooms.remove(roomId);
        if (room == null) return Map.of("message", "Room not found");

        // Notify the other participant
        String other = room.callerDeviceId().equals(deviceId)
                ? room.calleeDeviceId() : room.callerDeviceId();

        messagingTemplate.convertAndSend("/topic/webrtc/" + roomId, Map.of(
                "type",    "CALL_ENDED",
                "roomId",  roomId,
                "from",    deviceId
        ));
        if (other != null) {
            messagingTemplate.convertAndSend("/topic/alerts/" + other, Map.of(
                    "type", "CALL_ENDED", "roomId", roomId
            ));
        }

        log.info("[WEBRTC] Call ended in room {}", roomId);
        return Map.of("message", "Call ended", "roomId", roomId);
    }

    /** Get room info */
    public Optional<Map<String, Object>> getRoom(String roomId) {
        Room room = rooms.get(roomId);
        if (room == null) return Optional.empty();
        return Optional.of(Map.of(
                "roomId",    room.roomId(),
                "status",    room.status().name(),
                "callType",  room.callType(),
                "createdAt", room.createdAt().toString(),
                "expiresAt", room.expiresAt().toString(),
                "iceServers", getIceServers()
        ));
    }

    /**
     * STUN/TURN server config — uses free public STUN servers.
     * For production, add your own TURN server (Coturn is open source).
     */
    private List<Map<String, Object>> getIceServers() {
        return List.of(
            Map.of("urls", List.of("stun:stun.l.google.com:19302", "stun:stun1.l.google.com:19302")),
            Map.of("urls", List.of("stun:stun.stunprotocol.org:3478")),
            Map.of("urls", List.of("stun:openrelay.metered.ca:80"))
            // Production: add TURN servers here for NAT traversal
            // Map.of("urls", "turn:your-coturn-server:3478", "username", "...", "credential", "...")
        );
    }

    /** Clean up expired rooms (call this on a schedule) */
    public void cleanExpiredRooms() {
        LocalDateTime now = LocalDateTime.now();
        int removed = 0;
        for (Iterator<Map.Entry<String, Room>> it = rooms.entrySet().iterator(); it.hasNext();) {
            if (it.next().getValue().expiresAt().isBefore(now)) {
                it.remove();
                removed++;
            }
        }
        if (removed > 0) log.info("[WEBRTC] Cleaned {} expired rooms", removed);
    }
}
