package com.emergency.system.controller;

import com.emergency.system.service.AiDetectionService;
import com.emergency.system.service.AiDetectionService.DetectionResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * AI detection endpoints — called by Android when on-device model flags something.
 *
 * POST /api/ai/detect/frame   — analyze a camera frame
 * POST /api/ai/detect/signal  — report on-device detection (no image, low bandwidth)
 */
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI Detection", description = "Face/fall/panic detection via Claude Vision")
@RequiredArgsConstructor
public class AiDetectionController {

    private final AiDetectionService detectionService;

    /**
     * Full image analysis — use when bandwidth allows.
     * Body: { imageBase64, deviceId, latitude, longitude, deviceSignal }
     */
    @Operation(summary = "Analyze a camera frame for emergency signals")
    @PostMapping("/detect/frame")
    public ResponseEntity<DetectionResult> analyzeFrame(
            @RequestBody Map<String, Object> body) {

        String imageBase64  = (String) body.get("imageBase64");
        String deviceId     = (String) body.get("deviceId");
        String deviceSignal = (String) body.getOrDefault("deviceSignal", "NONE");
        double lat = body.containsKey("latitude")  ? ((Number) body.get("latitude")).doubleValue()  : 0.0;
        double lon = body.containsKey("longitude") ? ((Number) body.get("longitude")).doubleValue() : 0.0;

        if (imageBase64 == null || deviceId == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(
                detectionService.analyzeFrame(imageBase64, deviceId, lat, lon, deviceSignal));
    }

    /**
     * Lightweight signal report — no image, just the on-device classification.
     * Used in low-bandwidth or offline→online sync scenarios.
     * Body: { deviceId, latitude, longitude, signal: "FALL"|"PANIC_FACE"|"NONE" }
     */
    @Operation(summary = "Report on-device detection signal (no image required)")
    @PostMapping("/detect/signal")
    public ResponseEntity<DetectionResult> reportSignal(
            @RequestBody Map<String, Object> body) {

        String deviceId = (String) body.get("deviceId");
        String signal   = (String) body.getOrDefault("signal", "NONE");
        double lat = body.containsKey("latitude")  ? ((Number) body.get("latitude")).doubleValue()  : 0.0;
        double lon = body.containsKey("longitude") ? ((Number) body.get("longitude")).doubleValue() : 0.0;

        if (deviceId == null) return ResponseEntity.badRequest().build();

        return ResponseEntity.ok(detectionService.analyzeSignal(deviceId, lat, lon, signal));
    }
}
