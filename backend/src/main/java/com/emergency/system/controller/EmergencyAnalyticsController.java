package com.emergency.system.controller;

import com.emergency.system.service.EmergencyAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Analytics endpoints for dashboard charts.
 *
 * GET /api/analytics/timeline   — hourly counts (last 24h)
 * GET /api/analytics/risk       — by risk level
 * GET /api/analytics/status     — by status
 * GET /api/analytics/trend      — 7-day daily trend
 * GET /api/analytics/hotspots   — geographic clusters
 * GET /api/analytics/roles      — users by role
 */
@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Chart data for admin dashboard")
@RequiredArgsConstructor
public class EmergencyAnalyticsController {

    private final EmergencyAnalyticsService analyticsService;

    @Operation(summary = "Hourly emergency counts for the last 24 hours")
    @GetMapping("/timeline")
    public ResponseEntity<Map<String, Object>> timeline() {
        return ResponseEntity.ok(analyticsService.getTimeline());
    }

    @Operation(summary = "Emergency counts grouped by risk level")
    @GetMapping("/risk")
    public ResponseEntity<Map<String, Object>> byRisk() {
        return ResponseEntity.ok(analyticsService.getByRiskLevel());
    }

    @Operation(summary = "Emergency counts grouped by status")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> byStatus() {
        return ResponseEntity.ok(analyticsService.getByStatus());
    }

    @Operation(summary = "Daily emergency counts for the last 7 days with trend")
    @GetMapping("/trend")
    public ResponseEntity<Map<String, Object>> trend() {
        return ResponseEntity.ok(analyticsService.getTrend());
    }

    @Operation(summary = "Geographic hotspot clusters")
    @GetMapping("/hotspots")
    public ResponseEntity<Map<String, Object>> hotspots() {
        return ResponseEntity.ok(analyticsService.getHotspots());
    }

    @Operation(summary = "User count by role")
    @GetMapping("/roles")
    public ResponseEntity<Map<String, Object>> byRole() {
        return ResponseEntity.ok(analyticsService.getUsersByRole());
    }
}
