package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.AiAnalysisResult;
import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.model.Emergency.RiskLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AiAnalysisServiceTest {

    private AiAnalysisService service;

    @BeforeEach
    void setUp() {
        service = new AiAnalysisService(new ObjectMapper());
    }

    @Test
    @DisplayName("User confirmed safe → LOW risk")
    void userOk_returnsLow() {
        EmergencyRequest req = EmergencyRequest.builder()
                .userResponse("ARE_YOU_OK")
                .fallDetected(false)
                .movement("WALKING")
                .build();

        AiAnalysisResult result = service.analyzeEmergency(req);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.LOW);
        assertThat(result.getConfidenceScore()).isGreaterThanOrEqualTo(0.8); // Fixed here
    }

    @Test
    @DisplayName("User requested help → HIGH risk")
    void needHelp_returnsHigh() {
        EmergencyRequest req = EmergencyRequest.builder()
                .userResponse("NEED_HELP")
                .fallDetected(false)
                .movement("STATIONARY")
                .build();

        AiAnalysisResult result = service.analyzeEmergency(req);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
    }

    @Test
    @DisplayName("Fall + stationary + no response → HIGH risk")
    void fall_stationary_noResponse_returnsHigh() {
        EmergencyRequest req = EmergencyRequest.builder()
                .fallDetected(true)
                .movement("STATIONARY")
                .userResponse("NO_RESPONSE")
                .build();

        AiAnalysisResult result = service.analyzeEmergency(req);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.HIGH);
        assertThat(result.getConfidenceScore()).isGreaterThanOrEqualTo(0.8);
    }

    @Test
    @DisplayName("Fall + moving → MEDIUM risk")
    void fall_moving_returnsMedium() {
        EmergencyRequest req = EmergencyRequest.builder()
                .fallDetected(true)
                .movement("WALKING")
                .userResponse("NO_RESPONSE")
                .build();

        AiAnalysisResult result = service.analyzeEmergency(req);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
    }

    @Test
    @DisplayName("Stationary + no response (no fall) → LOW (likely phone drop)")
    void stationary_noResponse_noFall_returnsLow() {
        EmergencyRequest req = EmergencyRequest.builder()
                .fallDetected(false)
                .movement("STATIONARY")
                .userResponse("NO_RESPONSE")
                .build();

        AiAnalysisResult result = service.analyzeEmergency(req);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.LOW);
    }

    @Test
    @DisplayName("No signals → LOW risk")
    void noSignals_returnsLow() {
        EmergencyRequest req = EmergencyRequest.builder()
                .fallDetected(false)
                .movement("UNKNOWN")
                .userResponse(null)
                .build();

        AiAnalysisResult result = service.analyzeEmergency(req);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.LOW);
    }
}