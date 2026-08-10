package com.emergency.system.service;

import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Flexible search and filter for emergencies.
 * All filtering is done in-memory on H2; swap to @Query for PostgreSQL at scale.
 */
@Service
@RequiredArgsConstructor
public class EmergencySearchService {

    private final EmergencyRepository emergencyRepository;

    public record SearchParams(
            String keyword,
            Emergency.RiskLevel riskLevel,
            Emergency.EmergencyStatus status,
            String deviceId,
            LocalDateTime from,
            LocalDateTime to,
            Double lat, Double lon, Double radiusKm
    ) {}

    public List<Emergency> search(SearchParams p) {
        return emergencyRepository.findAll().stream()
                .filter(e -> p.keyword() == null || p.keyword().isBlank()
                        || (e.getMessage() != null && e.getMessage()
                                .toLowerCase().contains(p.keyword().toLowerCase()))
                        || (e.getAiAction() != null && e.getAiAction()
                                .toLowerCase().contains(p.keyword().toLowerCase())))
                .filter(e -> p.riskLevel() == null || e.getRiskLevel() == p.riskLevel())
                .filter(e -> p.status()    == null || e.getStatus()    == p.status())
                .filter(e -> p.deviceId()  == null || p.deviceId().isBlank()
                        || p.deviceId().equals(e.getReportedByDeviceId()))
                .filter(e -> p.from() == null || (e.getCreatedAt() != null
                        && !e.getCreatedAt().isBefore(p.from())))
                .filter(e -> p.to()   == null || (e.getCreatedAt() != null
                        && !e.getCreatedAt().isAfter(p.to())))
                .filter(e -> {
                    if (p.lat() == null || p.lon() == null || p.radiusKm() == null) return true;
                    if (e.getLatitude() == null || e.getLongitude() == null) return false;
                    double dist = LocationService.haversineDistance(
                            p.lat(), p.lon(), e.getLatitude(), e.getLongitude());
                    return dist <= p.radiusKm() * 1000;
                })
                .sorted((a, b) -> {
                    if (a.getCreatedAt() == null) return 1;
                    if (b.getCreatedAt() == null) return -1;
                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })
                .collect(Collectors.toList());
    }
}
