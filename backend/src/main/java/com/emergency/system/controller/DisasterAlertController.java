package com.emergency.system.controller;

import com.emergency.system.model.DisasterAlert;
import com.emergency.system.service.DisasterAlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Disaster alert endpoints.
 * GET  /api/disasters/active            — all active alerts (public)
 * GET  /api/disasters/nearby            — alerts affecting a GPS point
 * POST /api/disasters                   — admin creates manual alert
 * PATCH /api/disasters/{id}/resolve     — admin resolves
 */
@RestController
@RequestMapping("/api/disasters")
@Tag(name = "Disaster Alerts", description = "Earthquake, flood, fire and other disaster alerts")
@RequiredArgsConstructor
public class DisasterAlertController {

    private final DisasterAlertService disasterService;

    @Operation(summary = "Get all active disaster alerts")
    @GetMapping("/active")
    public ResponseEntity<List<DisasterAlert>> getActive() {
        return ResponseEntity.ok(disasterService.getActiveAlerts());
    }

    @Operation(summary = "Get disaster alerts affecting a GPS location")
    @GetMapping("/nearby")
    public ResponseEntity<List<DisasterAlert>> getNearby(
            @RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(disasterService.getAlertsForLocation(lat, lon));
    }

    @Operation(summary = "Create a manual disaster alert (admin only)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DisasterAlert> create(@RequestBody DisasterAlert alert) {
        return ResponseEntity.ok(disasterService.createManualAlert(alert));
    }

    @Operation(summary = "Resolve a disaster alert (admin only)")
    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DisasterAlert> resolve(@PathVariable Long id) {
        return ResponseEntity.ok(disasterService.resolveAlert(id));
    }
}
