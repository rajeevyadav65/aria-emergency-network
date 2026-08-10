package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.dto.EmergencyDTOs.EmergencyResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI-powered detection service using Claude Vision.
 *
 * Detects from image/video frame:
 * - Fear / panic on face
 * - Fall detection (body posture)
 * - Abnormal movement patterns
 *
 * ARCHITECTURE:
 * - Primary detection: On-device (Android ML Kit / TensorFlow Lite) — no internet needed
 * - Secondary: This service validates ambiguous cases via Claude Vision API
 * - Result triggers auto-emergency if confidence > threshold
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiDetectionService {

    @Value("${anthropic.api.key:}")
    private String anthropicApiKey;

    @Value("${app.ai.detection.confidence-threshold:0.75}")
    private double confidenceThreshold;

    @Value("${app.ai.detection.enabled:true}")
    private boolean detectionEnabled;

    private final EmergencyService emergencyService;
    private final ObjectMapper objectMapper;

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public record DetectionResult(
            String detectionType,   // FALL, PANIC_FACE, ABNORMAL_MOVEMENT, NONE
            double confidence,
            String description,
            boolean triggerEmergency,
            EmergencyResponse emergencyResponse
    ) {}

    /**
     * Analyses a base64-encoded image frame from the device camera.
     * Called when on-device model flags a suspicious situation.
     *
     * @param imageBase64  base64-encoded JPEG frame
     * @param deviceId     reporting device
     * @param latitude     current location
     * @param longitude    current location
     * @param deviceSignal pre-classification from on-device model (hint for Claude)
     */
    public DetectionResult analyzeFrame(String imageBase64, String deviceId,
                                         double latitude, double longitude,
                                         String deviceSignal) {

        if (!detectionEnabled) {
            return new DetectionResult("DISABLED", 0, "Detection disabled", false, null);
        }

        // If no API key, trust on-device signal directly
        if (anthropicApiKey == null || anthropicApiKey.isBlank()
                || anthropicApiKey.equals("your-api-key-here")) {
            return handleDeviceSignalFallback(deviceSignal, deviceId, latitude, longitude);
        }

        try {
            return analyzeWithClaude(imageBase64, deviceId, latitude, longitude, deviceSignal);
        } catch (Exception e) {
            log.warn("[DETECT] Claude Vision failed: {} — using device signal", e.getMessage());
            return handleDeviceSignalFallback(deviceSignal, deviceId, latitude, longitude);
        }
    }

    public DetectionResult analyzeSignal(String deviceId, double latitude, double longitude, String signal) {
        return handleDeviceSignalFallback(signal, deviceId, latitude, longitude);
    }

    private DetectionResult analyzeWithClaude(String imageBase64, String deviceId,
                                               double lat, double lon, String hint)
            throws Exception {

        String prompt = """
                Analyze this image for emergency safety concerns. Look for:
                1. FALL: Person lying on ground, fallen posture, or impact position
                2. PANIC_FACE: Extreme fear, distress, wide eyes, screaming expression
                3. ABNORMAL_MOVEMENT: Convulsion, seizure, struggling
                
                Device pre-classification hint: %s
                
                Respond ONLY with this JSON (no markdown):
                {
                  "detectionType": "FALL" | "PANIC_FACE" | "ABNORMAL_MOVEMENT" | "NONE",
                  "confidence": 0.0-1.0,
                  "description": "<brief description>",
                  "triggerEmergency": true|false
                }
                """.formatted(hint != null ? hint : "none");

        Map<String, Object> body = Map.of(
                "model", "claude-opus-4-5",
                "max_tokens", 200,
                "messages", List.of(Map.of("role", "user", "content", List.of(
                        Map.of("type", "image", "source", Map.of(
                                "type", "base64",
                                "media_type", "image/jpeg",
                                "data", imageBase64)),
                        Map.of("type", "text", "text", prompt)
                )))
        );

        Request req = new Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", anthropicApiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(body),
                        MediaType.parse("application/json")))
                .build();

        try (Response resp = HTTP.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null)
                throw new RuntimeException("API error " + resp.code());

            String text = objectMapper.readTree(resp.body().string())
                    .at("/content/0/text").asText();
            text = text.replaceAll("```json|```", "").trim();
            var result = objectMapper.readTree(text);

            String detType   = result.get("detectionType").asText();
            double conf      = result.get("confidence").asDouble();
            String desc      = result.get("description").asText();
            boolean trigger  = result.get("triggerEmergency").asBoolean();

            EmergencyResponse emergencyResp = null;
            if (trigger && conf >= confidenceThreshold) {
                emergencyResp = fireAutoEmergency(detType, desc, deviceId, lat, lon);
            }

            log.info("[DETECT] {} conf={} trigger={} device={}", detType, conf, trigger, deviceId);
            return new DetectionResult(detType, conf, desc, trigger && conf >= confidenceThreshold, emergencyResp);
        }
    }

    private DetectionResult handleDeviceSignalFallback(String signal, String deviceId,
                                                        double lat, double lon) {
        if (signal == null || "NONE".equalsIgnoreCase(signal)) {
            return new DetectionResult("NONE", 0, "No threat detected", false, null);
        }

        // Trust high-confidence on-device signals
        double conf = 0.80;
        EmergencyResponse resp = null;

        if ("FALL".equalsIgnoreCase(signal) || "PANIC_FACE".equalsIgnoreCase(signal)) {
            resp = fireAutoEmergency(signal, "Auto-detected: " + signal, deviceId, lat, lon);
        }

        return new DetectionResult(signal, conf, "On-device detection: " + signal, resp != null, resp);
    }

    private EmergencyResponse fireAutoEmergency(String type, String desc,
                                                  String deviceId, double lat, double lon) {
        String message = switch (type) {
            case "FALL"              -> "AI detected a fall — person may be injured";
            case "PANIC_FACE"        -> "AI detected panic/fear expression — possible danger";
            case "ABNORMAL_MOVEMENT" -> "AI detected abnormal movement — possible seizure";
            default                  -> "AI detected emergency: " + type;
        };

        EmergencyRequest req = EmergencyRequest.builder()
                .message(message + " | " + desc)
                .latitude(lat).longitude(lon)
                .fallDetected("FALL".equalsIgnoreCase(type))
                .movement("STATIONARY")
                .userResponse("NO_RESPONSE")
                .deviceId(deviceId)
                .build();

        log.warn("[DETECT] Auto-firing emergency: type={} device={}", type, deviceId);
        return emergencyService.processEmergency(req);
    }
}
