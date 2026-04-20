package com.emergency.system.service;

import com.emergency.system.model.Emergency;
import com.emergency.system.model.User;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Provides analytics data for charts, heatmaps, and trend analysis.
 *
 * Used by the admin dashboard and the React frontend's charts.
 *
 * GET /api/admin/analytics/timeline    — emergencies per hour (last 24h)
 * GET /api/admin/analytics/by-risk     — count by risk level
 * GET /api/admin/analytics/by-type     — movement type breakdown
 * GET /api/admin/analytics/hotspots    — geographic clusters
 * GET /api/admin/analytics/trend       — daily counts for last 7 days
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EmergencyAnalyticsService {

    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;

    private static final DateTimeFormatter HOUR_FMT  = DateTimeFormatter.ofPattern("HH:00");
    private static final DateTimeFormatter DAY_FMT   = DateTimeFormatter.ofPattern("EEE");
    private static final DateTimeFormatter DATE_FMT  = DateTimeFormatter.ofPattern("MMM dd");

    /** Emergencies per hour for the last 24 hours — for timeline chart */
    public Map<String, Object> getTimeline() {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        List<Emergency> recent = emergencyRepository.findRecentEmergencies(since);

        // Bucket by hour
        Map<String, Long> byHour = new LinkedHashMap<>();
        for (int h = 23; h >= 0; h--) {
            String label = LocalDateTime.now().minusHours(h).format(HOUR_FMT);
            byHour.put(label, 0L);
        }
        recent.forEach(e -> {
            if (e.getCreatedAt() != null) {
                String key = e.getCreatedAt().format(HOUR_FMT);
                byHour.merge(key, 1L, Long::sum);
            }
        });

        return Map.of(
                "labels", new ArrayList<>(byHour.keySet()),
                "values", new ArrayList<>(byHour.values()),
                "total",  recent.size(),
                "period", "last_24_hours"
        );
    }

    /** Emergency counts grouped by risk level — for donut chart */
    public Map<String, Object> getByRiskLevel() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Emergency.RiskLevel level : Emergency.RiskLevel.values()) {
            counts.put(level.name(), emergencyRepository.countByRiskLevel(level));
        }
        return Map.of(
                "labels", new ArrayList<>(counts.keySet()),
                "values", new ArrayList<>(counts.values())
        );
    }

    /** Emergencies count by status — for status breakdown */
    public Map<String, Object> getByStatus() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Emergency.EmergencyStatus status : Emergency.EmergencyStatus.values()) {
            counts.put(status.name(), emergencyRepository.countByStatus(status));
        }
        return Map.of(
                "labels", new ArrayList<>(counts.keySet()),
                "values", new ArrayList<>(counts.values())
        );
    }

    /** Daily emergency counts for last 7 days — for trend chart */
    public Map<String, Object> getTrend() {
        LocalDateTime since = LocalDateTime.now().minusDays(7);
        List<Emergency> week = emergencyRepository.findRecentEmergencies(since);

        Map<String, Long> byDay = new LinkedHashMap<>();
        for (int d = 6; d >= 0; d--) {
            LocalDateTime day = LocalDateTime.now().minusDays(d);
            byDay.put(day.format(DAY_FMT), 0L);
        }
        week.forEach(e -> {
            if (e.getCreatedAt() != null) {
                String key = e.getCreatedAt().format(DAY_FMT);
                if (byDay.containsKey(key)) byDay.merge(key, 1L, Long::sum);
            }
        });

        // Compute week-over-week change
        long thisWeek = week.size();
        List<Emergency> prevWeek = emergencyRepository.findRecentEmergencies(
                LocalDateTime.now().minusDays(14))
                .stream()
                .filter(e -> e.getCreatedAt() != null &&
                             e.getCreatedAt().isBefore(LocalDateTime.now().minusDays(7)))
                .collect(Collectors.toList());
        double change = prevWeek.isEmpty() ? 0.0 :
                Math.round(((thisWeek - prevWeek.size()) * 100.0 / prevWeek.size()) * 10.0) / 10.0;

        return Map.of(
                "labels",        new ArrayList<>(byDay.keySet()),
                "values",        new ArrayList<>(byDay.values()),
                "thisWeek",      thisWeek,
                "prevWeek",      prevWeek.size(),
                "changePercent", change
        );
    }

    /** Geographic hotspots — top 10 most active grid cells */
    public Map<String, Object> getHotspots() {
        List<Emergency> all = emergencyRepository.findActiveWithLocation();

        // Group into 0.01° grid cells (~1km²)
        Map<String, long[]> grid = new HashMap<>();
        all.forEach(e -> {
            if (e.getLatitude() != null && e.getLongitude() != null) {
                double latBucket = Math.round(e.getLatitude()  * 100.0) / 100.0;
                double lonBucket = Math.round(e.getLongitude() * 100.0) / 100.0;
                String key = latBucket + "," + lonBucket;
                grid.computeIfAbsent(key, k -> new long[]{(long)(latBucket * 1e6), (long)(lonBucket * 1e6), 0})[2]++;
            }
        });

        List<Map<String, Object>> hotspots = grid.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue()[2], a.getValue()[2]))
                .limit(10)
                .map(entry -> Map.<String, Object>of(
                        "latitude",  entry.getValue()[0] / 1e6,
                        "longitude", entry.getValue()[1] / 1e6,
                        "count",     entry.getValue()[2]
                ))
                .collect(Collectors.toList());

        return Map.of("hotspots", hotspots, "totalActive", all.size());
    }

    /** Role breakdown — how many users of each role */
    public Map<String, Object> getUsersByRole() {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (User.UserRole role : User.UserRole.values()) {
            counts.put(role.name(), (long) userRepository.findByRole(role).size());
        }
        return Map.of(
                "labels", new ArrayList<>(counts.keySet()),
                "values", new ArrayList<>(counts.values()),
                "total",  userRepository.count()
        );
    }
}
