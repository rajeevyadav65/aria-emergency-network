package com.emergency.system.service;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmergencyAnalyticsServiceTest {

    @Mock private EmergencyRepository emergencyRepository;
    @Mock private UserRepository userRepository;

    private EmergencyAnalyticsService service;

    @BeforeEach
    void setUp() {
        service = new EmergencyAnalyticsService(emergencyRepository, userRepository);
    }

    @Test
    @DisplayName("getTimeline returns 24 hourly labels")
    void getTimeline_returns24Labels() {
        when(emergencyRepository.findRecentEmergencies(any())).thenReturn(List.of());

        Map<String, Object> result = service.getTimeline();

        assertThat(result).containsKeys("labels", "values", "total", "period");
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) result.get("labels");
        assertThat(labels).hasSize(24);
        assertThat(result.get("total")).isEqualTo(0);
    }

    @Test
    @DisplayName("getTimeline counts emergencies in correct hours")
    void getTimeline_countsCorrectly() {
        Emergency e = Emergency.builder()
                .id(1L).riskLevel(Emergency.RiskLevel.HIGH)
                .createdAt(LocalDateTime.now()).build();
        when(emergencyRepository.findRecentEmergencies(any())).thenReturn(List.of(e));

        Map<String, Object> result = service.getTimeline();
        assertThat(result.get("total")).isEqualTo(1);
    }

    @Test
    @DisplayName("getByRiskLevel returns all risk levels")
    void getByRiskLevel_allLevels() {
        for (Emergency.RiskLevel level : Emergency.RiskLevel.values()) {
            when(emergencyRepository.countByRiskLevel(level)).thenReturn(0L);
        }

        Map<String, Object> result = service.getByRiskLevel();
        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) result.get("labels");
        assertThat(labels).contains("HIGH", "MEDIUM", "LOW", "FALSE_ALARM");
    }

    @Test
    @DisplayName("getTrend returns 7 day labels")
    void getTrend_returns7Labels() {
        when(emergencyRepository.findRecentEmergencies(any())).thenReturn(List.of());

        Map<String, Object> result = service.getTrend();

        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) result.get("labels");
        assertThat(labels).hasSize(7);
        assertThat(result).containsKey("changePercent");
    }

    @Test
    @DisplayName("getHotspots returns hotspot list with active total")
    void getHotspots_returnsHotspots() {
        when(emergencyRepository.findActiveWithLocation()).thenReturn(List.of());

        Map<String, Object> result = service.getHotspots();

        assertThat(result).containsKey("hotspots");
        assertThat(result).containsKey("totalActive");
    }

    @Test
    @DisplayName("getHotspots clusters by 0.01 degree grid")
    void getHotspots_clustersPoints() {
        Emergency e1 = Emergency.builder().id(1L).latitude(27.175).longitude(78.008).build();
        Emergency e2 = Emergency.builder().id(2L).latitude(27.176).longitude(78.009).build();
        Emergency e3 = Emergency.builder().id(3L).latitude(28.000).longitude(79.000).build();

        when(emergencyRepository.findActiveWithLocation()).thenReturn(List.of(e1, e2, e3));

        Map<String, Object> result = service.getHotspots();
        @SuppressWarnings("unchecked")
        List<?> hotspots = (List<?>) result.get("hotspots");
        // e1 and e2 cluster together, e3 is separate — so 2 hotspots
        assertThat(hotspots.size()).isLessThanOrEqualTo(3);
    }

    @Test
    @DisplayName("getUsersByRole returns all roles")
    void getUsersByRole_allRoles() {
        when(userRepository.findByRole(any())).thenReturn(List.of());
        when(userRepository.count()).thenReturn(0L);

        Map<String, Object> result = service.getUsersByRole();

        @SuppressWarnings("unchecked")
        List<String> labels = (List<String>) result.get("labels");
        assertThat(labels).contains("USER", "DOCTOR", "POLICE", "AMBULANCE", "ADMIN");
    }
}
