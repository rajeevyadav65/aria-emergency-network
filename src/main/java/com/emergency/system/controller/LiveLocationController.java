package com.emergency.system.controller;

import com.emergency.system.service.LiveLocationSharingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/live-location")
@Tag(name = "Live Location", description = "Real-time GPS sharing between victims and responders")
@RequiredArgsConstructor
public class LiveLocationController {

    private final LiveLocationSharingService locationService;

    @Operation(summary = "Update my GPS position and broadcast to active shares")
    @PostMapping("/update")
    public ResponseEntity<Void> update(@RequestBody Map<String, Object> body) {
        String deviceId = (String) body.get("deviceId");
        double lat = ((Number) body.get("latitude")).doubleValue();
        double lon = ((Number) body.get("longitude")).doubleValue();
        Float accuracy = body.containsKey("accuracy")
                ? ((Number) body.get("accuracy")).floatValue() : null;
        Float speed = body.containsKey("speed")
                ? ((Number) body.get("speed")).floatValue() : null;

        locationService.broadcastLocation(deviceId, lat, lon, accuracy, speed);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Share your live location with an ambulance, police or doctor")
    @PostMapping("/share")
    public ResponseEntity<Map<String, Object>> share(@RequestBody Map<String, Object> body) {
        String deviceId = (String) body.get("deviceId");
        Long responderUserId = Long.valueOf(body.get("responderUserId").toString());
        double lat = ((Number) body.get("latitude")).doubleValue();
        double lon = ((Number) body.get("longitude")).doubleValue();
        Float accuracy = body.containsKey("accuracy")
                ? ((Number) body.get("accuracy")).floatValue() : null;

        Map<String, Object> result = locationService.startSharing(deviceId, responderUserId);
        locationService.broadcastLocation(deviceId, lat, lon, accuracy, null);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Stop sharing with a specific responder")
    @DeleteMapping("/share/{deviceId}/{responderUserId}")
    public ResponseEntity<Map<String, String>> stopShare(
            @PathVariable String deviceId,
            @PathVariable Long responderUserId) {
        locationService.stopSharing(deviceId, responderUserId);
        return ResponseEntity.ok(Map.of("message", "Location sharing stopped"));
    }

    @Operation(summary = "Get my active location shares")
    @GetMapping("/shares")
    public ResponseEntity<Map<String, Object>> getShares(@RequestParam String deviceId) {
        return ResponseEntity.ok(locationService.getShareStatus(deviceId));
    }

    @Operation(summary = "Responder: get the latest GPS position of a victim")
    @GetMapping("/track")
    public ResponseEntity<Map<String, Object>> track(@RequestParam String deviceId) {
        return ResponseEntity.ok(locationService.getLatestLocation(deviceId));
    }

    @Operation(summary = "Responder: push own GPS position + ETA to victim")
    @PostMapping("/responder")
    public ResponseEntity<Void> responderUpdate(@RequestBody Map<String, Object> body) {
        Long responderUserId = Long.valueOf(body.get("responderUserId").toString());
        double lat = ((Number) body.get("latitude")).doubleValue();
        double lon = ((Number) body.get("longitude")).doubleValue();
        String eta = (String) body.getOrDefault("etaMinutes", "unknown");

        locationService.broadcastResponderLocation(responderUserId, lat, lon, eta);
        return ResponseEntity.ok().build();
    }
}
