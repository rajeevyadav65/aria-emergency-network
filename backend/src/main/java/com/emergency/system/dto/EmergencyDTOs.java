package com.emergency.system.dto;

import com.emergency.system.model.Emergency;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * All request/response DTOs grouped for simplicity.
 */
public class EmergencyDTOs {

    // ── Auth ──────────────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class RegisterRequest {
        private String name;
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        private String password;
        private String deviceId;
        private String role;
        private String licenseNumber;
        private String specialization;
        private String vehicleId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        private String email;
        @NotBlank(message = "Password is required")
        private String password;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AuthResponse {
        private String token;
        private String deviceId;
        private String role;
        private String name;
        private String email;
    }

    // ── Emergency ─────────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true) // 🟢 THE MAGIC FIX ADDED HERE
    public static class EmergencyRequest {
        @Size(max = 1000, message = "Message too long")
        private String message;
        @NotNull(message = "Latitude is required")
        private Double latitude;
        @NotNull(message = "Longitude is required")
        private Double longitude;
        private Boolean fallDetected;
        private String movement;
        private String userResponse;
        private String deviceId;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class EmergencyResponse {
        private Long emergencyId;
        private Emergency.RiskLevel riskLevel;
        private String aiAction;
        private Emergency.EmergencyStatus status;
        private int nearbyUsersAlerted;
        private String message;
    }

    // ── AI Analysis ───────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AiAnalysisResult {
        private Emergency.RiskLevel riskLevel;
        private String action;
        private double confidenceScore;
        private String reasoning;
    }

    // ── Location ──────────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class LocationUpdateRequest {
        private Double latitude;
        private Double longitude;
        private String deviceId;
    }

    // ── Alert ─────────────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class AlertNotification {
        private Long alertId;
        private Long emergencyId;
        private Emergency.RiskLevel riskLevel;
        private String message;
        private Double emergencyLatitude;
        private Double emergencyLongitude;
        private Double distanceMeters;
        private LocalDateTime sentAt;
    }

    // ── Chat ──────────────────────────────────────────────────────────────────

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatRequest {
        private String sessionId;
        private String message;
        private String context;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatResponse {
        private String reply;
        private String sessionId;
        private LocalDateTime timestamp;
        private boolean isEmergency;
        private String suggestedAction;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatHistoryResponse {
        private String sessionId;
        private List<ChatMessageDto> messages;
    }

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ChatMessageDto {
        private String role;
        private String content;
        private LocalDateTime createdAt;
    }
}