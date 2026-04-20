package com.emergency.system.service;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmergencySearchServiceTest {

    @Mock private EmergencyRepository emergencyRepository;
    private EmergencySearchService service;

    private final List<Emergency> testData = List.of(
        Emergency.builder().id(1L).message("Person fell near market").riskLevel(Emergency.RiskLevel.HIGH)
            .status(Emergency.EmergencyStatus.ACTIVE).reportedByDeviceId("dev-a")
            .latitude(27.1767).longitude(78.0081).createdAt(LocalDateTime.now().minusMinutes(5)).build(),
        Emergency.builder().id(2L).message("Minor road accident").riskLevel(Emergency.RiskLevel.MEDIUM)
            .status(Emergency.EmergencyStatus.PENDING).reportedByDeviceId("dev-b")
            .latitude(27.1800).longitude(78.0100).createdAt(LocalDateTime.now().minusMinutes(20)).build(),
        Emergency.builder().id(3L).message("False alarm").riskLevel(Emergency.RiskLevel.LOW)
            .status(Emergency.EmergencyStatus.FALSE_ALARM).reportedByDeviceId("dev-a")
            .latitude(27.1850).longitude(78.0150).createdAt(LocalDateTime.now().minusHours(2)).build()
    );

    @BeforeEach void setUp() {
        service = new EmergencySearchService(emergencyRepository);
        when(emergencyRepository.findAll()).thenReturn(testData);
    }

    @Test
    @DisplayName("No filters → returns all sorted newest first")
    void noFilters_returnsAll() {
        var results = service.search(new EmergencySearchService.SearchParams(
                null,null,null,null,null,null,null,null,null));
        assertThat(results).hasSize(3);
        // newest first
        assertThat(results.get(0).getId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("Keyword filter matches message text")
    void keywordFilter() {
        var results = service.search(new EmergencySearchService.SearchParams(
                "fell",null,null,null,null,null,null,null,null));
        assertThat(results).hasSize(1).extracting(Emergency::getId).containsExactly(1L);
    }

    @Test
    @DisplayName("RiskLevel filter returns only matching")
    void riskLevelFilter() {
        var results = service.search(new EmergencySearchService.SearchParams(
                null,Emergency.RiskLevel.HIGH,null,null,null,null,null,null,null));
        assertThat(results).hasSize(1).allMatch(e -> e.getRiskLevel() == Emergency.RiskLevel.HIGH);
    }

    @Test
    @DisplayName("Status filter returns only ACTIVE")
    void statusFilter() {
        var results = service.search(new EmergencySearchService.SearchParams(
                null,null,Emergency.EmergencyStatus.ACTIVE,null,null,null,null,null,null));
        assertThat(results).hasSize(1).allMatch(e -> e.getStatus() == Emergency.EmergencyStatus.ACTIVE);
    }

    @Test
    @DisplayName("DeviceId filter returns only that device's emergencies")
    void deviceIdFilter() {
        var results = service.search(new EmergencySearchService.SearchParams(
                null,null,null,"dev-a",null,null,null,null,null));
        assertThat(results).hasSize(2).allMatch(e -> "dev-a".equals(e.getReportedByDeviceId()));
    }

    @Test
    @DisplayName("Geo radius filter 200m from origin → finds only very close ones")
    void geoRadiusFilter_small() {
        var results = service.search(new EmergencySearchService.SearchParams(
                null,null,null,null,null,null,27.1767,78.0081,0.2));
        // #1 is at origin, #2 is ~400m away, #3 is ~1km away
        assertThat(results).hasSize(1).extracting(Emergency::getId).containsExactly(1L);
    }

    @Test
    @DisplayName("Combined keyword + status filter")
    void combinedFilter() {
        var results = service.search(new EmergencySearchService.SearchParams(
                "accident",null,Emergency.EmergencyStatus.PENDING,null,null,null,null,null,null));
        assertThat(results).hasSize(1).extracting(Emergency::getId).containsExactly(2L);
    }
}
