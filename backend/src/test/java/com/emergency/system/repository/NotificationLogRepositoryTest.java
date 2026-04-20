package com.emergency.system.repository;

import com.emergency.system.model.NotificationLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NotificationLogRepositoryTest {

    @Autowired
    private NotificationLogRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        repo.save(NotificationLog.builder()
                .type(NotificationLog.NotificationType.EMERGENCY_ALERT)
                .emergencyId(1L).recipientDeviceId("dev-001")
                .channel("WEBSOCKET").message("Test alert").delivered(true).build());
        repo.save(NotificationLog.builder()
                .type(NotificationLog.NotificationType.EMERGENCY_ALERT)
                .emergencyId(1L).recipientDeviceId("dev-002")
                .channel("WEBSOCKET").message("Test alert 2")
                .delivered(false).failureReason("WS disconnected").build());
        repo.save(NotificationLog.builder()
                .type(NotificationLog.NotificationType.HIGH_RISK_BROADCAST)
                .emergencyId(2L).recipientDeviceId("BROADCAST")
                .channel("WEBSOCKET").message("Broadcast msg").delivered(true).build());
    }

    @Test
    @DisplayName("countByDelivered(true) returns 2")
    void countDelivered() {
        assertThat(repo.countByDelivered(true)).isEqualTo(2);
        assertThat(repo.countByDelivered(false)).isEqualTo(1);
    }

    @Test
    @DisplayName("findByEmergencyId returns correct logs")
    void findByEmergencyId() {
        assertThat(repo.findByEmergencyId(1L)).hasSize(2);
        assertThat(repo.findByEmergencyId(99L)).isEmpty();
    }

    @Test
    @DisplayName("countSince returns logs in time window")
    void countSince() {
        long count = repo.countSince(LocalDateTime.now().minusMinutes(1));
        assertThat(count).isEqualTo(3);

        long oldCount = repo.countSince(LocalDateTime.now().plusHours(1));
        assertThat(oldCount).isEqualTo(0);
    }

    @Test
    @DisplayName("findByRecipientDeviceId finds correct records")
    void findByDevice() {
        assertThat(repo.findByRecipientDeviceId("dev-001")).hasSize(1);
        assertThat(repo.findByRecipientDeviceId("BROADCAST")).hasSize(1);
    }
}
