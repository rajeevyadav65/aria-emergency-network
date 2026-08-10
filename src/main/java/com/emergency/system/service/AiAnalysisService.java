package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.AiAnalysisResult;
import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.model.Emergency.RiskLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AiAnalysisService {

    @Value("${anthropic.api.key:}")
    private String anthropicApiKey;

    @Value("${anthropic.api.model:claude-3-sonnet-20240229}")
    private String model;

    private final ObjectMapper objectMapper;

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

    public AiAnalysisResult analyzeEmergency(EmergencyRequest request) {
        // 🟢 Check if API Key is actually provided in application.properties
        if (anthropicApiKey != null && !anthropicApiKey.isBlank()
                && !anthropicApiKey.contains("your-api-key")) {
            try {
                return aiClassify(request);
            } catch (Exception e) {
                log.warn("AI classification failed, using fallback: {}", e.getMessage());
            }
        }
        return ruleBasedFallback(request);
    }

    private AiAnalysisResult aiClassify(EmergencyRequest req) throws Exception {
        String prompt = String.format("""
                You are an emergency triage AI. Classify this incident and respond ONLY with valid JSON.
                Incident data:
                - Message: "%s"
                - Fall detected: %s
                - Movement: %s
                - User response: %s
                - Location: (%.4f, %.4f)
                
                Respond with:
                {
                  "riskLevel": "HIGH" | "MEDIUM" | "LOW",
                  "action": "<one sentence action>",
                  "confidenceScore": <0.0-1.0>,
                  "reasoning": "<brief reasoning>"
                }
                """,
                req.getMessage() != null ? req.getMessage() : "none",
                req.getFallDetected(),
                req.getMovement(),
                req.getUserResponse(),
                req.getLatitude(),
                req.getLongitude());

        String body = objectMapper.writeValueAsString(Map.of(
                "model", model,
                "max_tokens", 300,
                "messages", new Object[]{Map.of("role", "user", "content", prompt)}
        ));

        Request httpReq = new Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", anthropicApiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(body, MediaType.parse("application/json")))
                .build();

        try (Response response = HTTP.newCall(httpReq).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("API error: " + response.code());
            String responseBody = response.body().string();
            var parsed = objectMapper.readTree(responseBody);
            String text = parsed.at("/content/0/text").asText();
            text = text.replaceAll("```json|```", "").trim();
            var result = objectMapper.readTree(text);

            return AiAnalysisResult.builder()
                    .riskLevel(RiskLevel.valueOf(result.get("riskLevel").asText()))
                    .action(result.get("action").asText())
                    .confidenceScore(result.get("confidenceScore").asDouble())
                    .reasoning(result.get("reasoning").asText())
                    .build();
        }
    }

    private AiAnalysisResult ruleBasedFallback(EmergencyRequest req) {
        boolean fall = Boolean.TRUE.equals(req.getFallDetected());
        boolean stationary = "STATIONARY".equalsIgnoreCase(req.getMovement()) || "FALLING".equalsIgnoreCase(req.getMovement());
        boolean noResponse = req.getUserResponse() == null || "NONE".equalsIgnoreCase(req.getUserResponse()) || "NO_RESPONSE".equalsIgnoreCase(req.getUserResponse());

        if ("NEED_HELP".equalsIgnoreCase(req.getUserResponse())) return build(RiskLevel.HIGH, "User requested immediate help", 1.0);
        if (fall && stationary) return build(RiskLevel.HIGH, "Fall detected and user is not moving", 0.9);
        if (fall) return build(RiskLevel.MEDIUM, "Fall detected, monitoring user", 0.7);

        return build(RiskLevel.LOW, "Normal monitoring", 0.8);
    }

    private AiAnalysisResult build(RiskLevel level, String action, double confidence) {
        return AiAnalysisResult.builder()
                .riskLevel(level).action(action)
                .confidenceScore(confidence).reasoning("Rule-based triage")
                .build();
    }
}