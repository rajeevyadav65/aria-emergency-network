package com.emergency.system.config;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AriaHealthIndicatorTest {

    @Mock private EmergencyRepository emergencyRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private AriaHealthIndicator indicator;

    @Test
    @DisplayName("health() returns UP when DB is accessible")
    void health_dbAccessible_returnsUp() {
        when(userRepository.count()).thenReturn(4L);
        when(emergencyRepository.findByStatus(Emergency.EmergencyStatus.ACTIVE))
                .thenReturn(List.of());
        when(emergencyRepository.count()).thenReturn(10L);

        Health h = indicator.health();

        assertThat(h.getStatus()).isEqualTo(Status.UP);
        assertThat(h.getDetails()).containsKey("database");
        assertThat(h.getDetails().get("database")).isEqualTo("UP");
        assertThat(h.getDetails()).containsKey("totalUsers");
        assertThat(h.getDetails().get("totalUsers")).isEqualTo(4L);
    }

    @Test
    @DisplayName("health() returns DOWN when DB throws exception")
    void health_dbDown_returnsDown() {
        when(userRepository.count()).thenThrow(new RuntimeException("DB connection refused"));

        Health h = indicator.health();

        assertThat(h.getStatus()).isEqualTo(Status.DOWN);
        assertThat(h.getDetails()).containsKey("database");
        assertThat(h.getDetails().get("database")).isEqualTo("UNREACHABLE");
    }

    @Test
    @DisplayName("health() includes activeEmergencies count")
    void health_includesActiveEmergencyCount() {
        when(userRepository.count()).thenReturn(2L);
        when(emergencyRepository.count()).thenReturn(5L);

        Emergency mockEmergency = Emergency.builder()
                .id(1L).status(Emergency.EmergencyStatus.ACTIVE).build();
        when(emergencyRepository.findByStatus(Emergency.EmergencyStatus.ACTIVE))
                .thenReturn(List.of(mockEmergency));

        Health h = indicator.health();

        assertThat(h.getStatus()).isEqualTo(Status.UP);
        assertThat(h.getDetails().get("activeEmergencies")).isEqualTo(1L); // Fixed here
    }
}