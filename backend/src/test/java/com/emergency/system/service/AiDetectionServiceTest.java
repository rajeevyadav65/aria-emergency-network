package com.emergency.system.service;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiDetectionServiceTest {

    @Mock private EmergencyService emergencyService;

    private AiDetectionService service;

    @BeforeEach
    void setUp() {
        service = new AiDetectionService(emergencyService, new ObjectMapper());
        ReflectionTestUtils.setField(service, "anthropicApiKey", "");   // no API key → fallback
        ReflectionTestUtils.setField(service, "confidenceThreshold", 0.75);
        ReflectionTestUtils.setField(service, "detectionEnabled", true);
    }

    @Test
    @DisplayName("analyzeFrame with NONE signal returns NONE detection")
    void analyzeFrame_noneSignal_returnsNone() {
        AiDetectionService.DetectionResult result =
                service.analyzeFrame(null, "dev-001", 27.17, 78.00, "NONE");

        assertThat(result.detectionType()).isEqualTo("NONE");
        assertThat(result.triggerEmergency()).isFalse();
    }

    @Test
    @DisplayName("analyzeFrame with FALL signal triggers emergency")
    void analyzeFrame_fallSignal_triggersEmergency() {
        var mockResponse = com.emergency.system.dto.EmergencyDTOs.EmergencyResponse.builder()
                .emergencyId(42L).riskLevel(Emergency.RiskLevel.HIGH).nearbyUsersAlerted(3).build();
        when(emergencyService.processEmergency(any())).thenReturn(mockResponse);

        AiDetectionService.DetectionResult result =
                service.analyzeFrame(null, "dev-002", 27.17, 78.00, "FALL");

        assertThat(result.detectionType()).isEqualTo("FALL");
        assertThat(result.triggerEmergency()).isTrue();
        assertThat(result.emergencyResponse()).isNotNull();
        assertThat(result.emergencyResponse().getEmergencyId()).isEqualTo(42L);
        verify(emergencyService).processEmergency(argThat(req ->
                Boolean.TRUE.equals(req.getFallDetected()) &&
                "dev-002".equals(req.getDeviceId())));
    }

    @Test
    @DisplayName("analyzeFrame with PANIC_FACE signal triggers emergency")
    void analyzeFrame_panicFace_triggersEmergency() {
        var mockResponse = com.emergency.system.dto.EmergencyDTOs.EmergencyResponse.builder()
                .emergencyId(43L).riskLevel(Emergency.RiskLevel.HIGH).nearbyUsersAlerted(2).build();
        when(emergencyService.processEmergency(any())).thenReturn(mockResponse);

        AiDetectionService.DetectionResult result =
                service.analyzeFrame(null, "dev-003", 27.17, 78.00, "PANIC_FACE");

        assertThat(result.detectionType()).isEqualTo("PANIC_FACE");
        assertThat(result.triggerEmergency()).isTrue();
        verify(emergencyService).processEmergency(any());
    }

    @Test
    @DisplayName("analyzeFrame when detection disabled returns DISABLED type")
    void analyzeFrame_disabled_returnsDisabled() {
        ReflectionTestUtils.setField(service, "detectionEnabled", false);

        AiDetectionService.DetectionResult result =
                service.analyzeFrame(null, "dev-004", 0.0, 0.0, "FALL");

        assertThat(result.detectionType()).isEqualTo("DISABLED");
        assertThat(result.triggerEmergency()).isFalse();
        verify(emergencyService, never()).processEmergency(any());
    }

    @Test
    @DisplayName("analyzeFrame null signal treated as NONE — no emergency")
    void analyzeFrame_nullSignal_treatedAsNone() {
        AiDetectionService.DetectionResult result =
                service.analyzeFrame(null, "dev-005", 0.0, 0.0, null);

        assertThat(result.triggerEmergency()).isFalse();
        verify(emergencyService, never()).processEmergency(any());
    }

    @Test
    @DisplayName("analyzeFrame emergency message contains detection type")
    void analyzeFrame_emergencyMessage_containsType() {
        var mockResponse = com.emergency.system.dto.EmergencyDTOs.EmergencyResponse.builder()
                .emergencyId(44L).riskLevel(Emergency.RiskLevel.HIGH).nearbyUsersAlerted(1).build();
        when(emergencyService.processEmergency(any())).thenReturn(mockResponse);

        service.analyzeFrame(null, "dev-006", 27.17, 78.00, "FALL");

        verify(emergencyService).processEmergency(argThat(req ->
                req.getMessage() != null &&
                req.getMessage().toLowerCase().contains("fall")));
    }
}
