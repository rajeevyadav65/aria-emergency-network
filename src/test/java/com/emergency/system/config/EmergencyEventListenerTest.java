package com.emergency.system.config;

import com.emergency.system.config.EmergencyEventListener.*;
import com.emergency.system.model.Emergency;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatNoException;

/**
 * Integration tests for the application event system.
 * Verifies events are published and listeners run without errors.
 */
@SpringBootTest
@ActiveProfiles("test")
class EmergencyEventListenerTest {

    @Autowired
    private ApplicationEventPublisher publisher;

    @Autowired
    private EmergencyEventPublisher emergencyEventPublisher;

    private Emergency sampleEmergency() {
        return Emergency.builder()
                .id(99L)
                .message("Test emergency")
                .latitude(27.1767)
                .longitude(78.0081)
                .riskLevel(Emergency.RiskLevel.HIGH)
                .status(Emergency.EmergencyStatus.ACTIVE)
                .build();
    }

    @Test
    @DisplayName("publishCreated() fires without exception")
    void publishCreated_noException() {
        assertThatNoException().isThrownBy(() ->
                emergencyEventPublisher.publishCreated(this, sampleEmergency()));
    }

    @Test
    @DisplayName("publishResolved() fires without exception")
    void publishResolved_noException() {
        Emergency e = sampleEmergency();
        e.setStatus(Emergency.EmergencyStatus.RESOLVED);
        assertThatNoException().isThrownBy(() ->
                emergencyEventPublisher.publishResolved(this, e));
    }

    @Test
    @DisplayName("publishHighRisk() fires without exception")
    void publishHighRisk_noException() {
        assertThatNoException().isThrownBy(() ->
                emergencyEventPublisher.publishHighRisk(this, sampleEmergency(), 5));
    }

    @Test
    @DisplayName("Direct ApplicationEventPublisher publishes EmergencyCreatedEvent")
    void directPublish_createdEvent() {
        assertThatNoException().isThrownBy(() ->
                publisher.publishEvent(new EmergencyCreatedEvent(this, sampleEmergency())));
    }

    @Test
    @DisplayName("Direct ApplicationEventPublisher publishes HighRiskEmergencyEvent")
    void directPublish_highRiskEvent() {
        assertThatNoException().isThrownBy(() ->
                publisher.publishEvent(new HighRiskEmergencyEvent(this, sampleEmergency(), 3)));
    }
}
