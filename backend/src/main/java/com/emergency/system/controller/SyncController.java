package com.emergency.system.controller;

import com.emergency.system.service.OfflineSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Offline → Online synchronisation endpoint.
 *
 * POST /api/sync/batch      — sync a batch of offline records
 * GET  /api/sync/pending    — count pending items for a device
 * POST /api/sync/retry      — trigger retry of failed items (admin)
 */
@RestController
@RequestMapping("/api/sync")
@Tag(name = "Offline Sync", description = "Offline-to-online data synchronisation")
@RequiredArgsConstructor
public class SyncController {

    private final OfflineSyncService syncService;

    /**
     * Accepts a batch of records created while the device was offline.
     *
     * Request body:
     * {
     *   "deviceId": "dev-abc123",
     *   "items": [
     *     {
     *       "localId": "uuid-offline-001",
     *       "type": "EMERGENCY_REPORT",
     *       "payload": { ...EmergencyRequest fields... }
     *     },
     *     ...
     *   ]
     * }
     */
    @Operation(summary = "Sync a batch of offline-created records")
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> syncBatch(@RequestBody Map<String, Object> body) {
        String deviceId = (String) body.get("deviceId");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");

        if (deviceId == null || items == null || items.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "deviceId and items are required"));
        }
        return ResponseEntity.ok(syncService.syncBatch(deviceId, items));
    }

    @Operation(summary = "Get count of pending sync items for a device")
    @GetMapping("/pending")
    public ResponseEntity<Map<String, Object>> pendingCount(@RequestParam String deviceId) {
        return ResponseEntity.ok(Map.of(
                "deviceId", deviceId,
                "pendingCount", syncService.getPendingCount(deviceId)
        ));
    }

    @Operation(summary = "Retry failed sync items (admin)")
    @PostMapping("/retry")
    public ResponseEntity<Map<String, String>> retry() {
        syncService.retryFailed();
        return ResponseEntity.ok(Map.of("message", "Retry scheduled"));
    }
}
