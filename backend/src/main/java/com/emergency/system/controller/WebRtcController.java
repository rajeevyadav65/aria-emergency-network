package com.emergency.system.controller;

import com.emergency.system.service.WebRtcSignalingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Custom WebRTC Signaling API — no Twilio/Agora needed.
 *
 * POST /api/webrtc/room           — create a video/voice call room
 * GET  /api/webrtc/room/{id}      — get room details + ICE servers
 * POST /api/webrtc/signal/offer   — send SDP offer
 * POST /api/webrtc/signal/answer  — send SDP answer
 * POST /api/webrtc/signal/ice     — relay ICE candidate
 * POST /api/webrtc/end            — end call
 */
@RestController
@RequestMapping("/api/webrtc")
@Tag(name = "WebRTC Signaling", description = "Custom peer-to-peer video/voice call signaling — no external service")
@RequiredArgsConstructor
public class WebRtcController {

    private final WebRtcSignalingService signalingService;

    @Operation(summary = "Create a video/voice call room")
    @PostMapping("/room")
    public ResponseEntity<Map<String, Object>> createRoom(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(signalingService.createRoom(
                body.get("callerDeviceId"),
                body.get("calleeDeviceId"),
                body.getOrDefault("callType", "VIDEO")
        ));
    }

    @Operation(summary = "Get room details and ICE server config")
    @GetMapping("/room/{roomId}")
    public ResponseEntity<Map<String, Object>> getRoom(@PathVariable String roomId) {
        return signalingService.getRoom(roomId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Send WebRTC SDP offer")
    @PostMapping("/signal/offer")
    public ResponseEntity<Map<String, String>> sendOffer(@RequestBody Map<String, String> body) {
        signalingService.sendOffer(body.get("roomId"), body.get("sdp"), body.get("deviceId"));
        return ResponseEntity.ok(Map.of("status", "offer relayed"));
    }

    @Operation(summary = "Send WebRTC SDP answer")
    @PostMapping("/signal/answer")
    public ResponseEntity<Map<String, String>> sendAnswer(@RequestBody Map<String, String> body) {
        signalingService.sendAnswer(body.get("roomId"), body.get("sdp"), body.get("deviceId"));
        return ResponseEntity.ok(Map.of("status", "answer relayed"));
    }

    @Operation(summary = "Relay ICE candidate between peers")
    @PostMapping("/signal/ice")
    public ResponseEntity<Map<String, String>> sendIce(@RequestBody Map<String, Object> body) {
        signalingService.sendIceCandidate(
                (String) body.get("roomId"),
                (String) body.get("candidate"),
                (String) body.getOrDefault("sdpMid", ""),
                body.containsKey("sdpMLineIndex") ? ((Number) body.get("sdpMLineIndex")).intValue() : 0,
                (String) body.get("deviceId")
        );
        return ResponseEntity.ok(Map.of("status", "ice relayed"));
    }

    @Operation(summary = "End a call and close the room")
    @PostMapping("/end")
    public ResponseEntity<Map<String, Object>> endCall(@RequestBody Map<String, String> body) {
        return ResponseEntity.ok(signalingService.endCall(body.get("roomId"), body.get("deviceId")));
    }

    /** Clean expired rooms every 10 minutes */
    @Scheduled(fixedDelay = 600_000)
    public void cleanRooms() {
        signalingService.cleanExpiredRooms();
    }
}
