package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.model.OfflineSyncQueue;
import com.emergency.system.repository.OfflineSyncQueueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Manages the OFFLINE → ONLINE sync pipeline.
 *
 * When a device reconnects:
 * 1. Device sends its pending offline queue via POST /api/sync/batch
 * 2. This service processes each item in order
 * 3. Returns a map of localId → serverAssignedId for client reconciliation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class OfflineSyncService {

    private final OfflineSyncQueueRepository syncRepo;
    private final EmergencyService emergencyService;
    private final LocationService locationService;
    private final ObjectMapper objectMapper;

    private static final int MAX_RETRIES = 3;

    /**
     * Accepts a batch of offline-created records from a device.
     * Processes them in creation order and returns ID mappings.
     */
    @Transactional
    public Map<String, Object> syncBatch(String deviceId, List<Map<String, Object>> items) {
        int accepted = 0, processed = 0, failed = 0;
        Map<String, String> idMap = new java.util.LinkedHashMap<>();

        for (Map<String, Object> item : items) {
            try {
                String localId   = (String) item.get("localId");
                String typeStr   = (String) item.get("type");
                String payloadJson = objectMapper.writeValueAsString(item.get("payload"));

                OfflineSyncQueue.SyncType type = OfflineSyncQueue.SyncType.valueOf(typeStr);

                // Check if already synced (idempotency)
                if (syncRepo.findByLocalId(localId).isPresent()) {
                    idMap.put(localId, "ALREADY_SYNCED");
                    continue;
                }

                OfflineSyncQueue record = syncRepo.save(OfflineSyncQueue.builder()
                        .deviceId(deviceId).type(type)
                        .payload(payloadJson).localId(localId)
                        .syncStatus(OfflineSyncQueue.SyncStatus.SYNCING)
                        .build());
                accepted++;

                String serverId = processRecord(record, payloadJson, type, deviceId);
                record.setSyncStatus(OfflineSyncQueue.SyncStatus.SYNCED);
                record.setServerAssignedId(serverId);
                record.setSyncedAt(LocalDateTime.now());
                syncRepo.save(record);
                idMap.put(localId, serverId);
                processed++;

            } catch (Exception e) {
                log.error("Sync item failed: {}", e.getMessage());
                failed++;
            }
        }

        log.info("[SYNC] Device {} — accepted:{} processed:{} failed:{}", deviceId, accepted, processed, failed);
        return Map.of(
                "accepted", accepted, "processed", processed, "failed", failed,
                "idMappings", idMap
        );
    }

    private String processRecord(OfflineSyncQueue record, String payload,
                                  OfflineSyncQueue.SyncType type, String deviceId) throws Exception {
        return switch (type) {
            case EMERGENCY_REPORT -> {
                EmergencyRequest req = objectMapper.readValue(payload, EmergencyRequest.class);
                req.setDeviceId(deviceId);
                var resp = emergencyService.processEmergency(req);
                yield String.valueOf(resp.getEmergencyId());
            }
            case LOCATION_UPDATE -> {
                var node = objectMapper.readTree(payload);
                locationService.updateUserLocation(
                        deviceId,
                        node.get("latitude").asDouble(),
                        node.get("longitude").asDouble());
                yield "LOCATION_UPDATED";
            }
            default -> {
                log.info("Sync type {} — no handler, marking as synced", type);
                yield "NO_ACTION";
            }
        };
    }

    /** Returns pending sync count for a device (used by mobile status bar) */
    public long getPendingCount(String deviceId) {
        return syncRepo.countByDeviceIdAndSyncStatus(deviceId, OfflineSyncQueue.SyncStatus.PENDING);
    }

    /** Retries failed sync items */
    @Async
    public void retryFailed() {
        List<OfflineSyncQueue> failed = syncRepo.findBySyncStatusAndRetryCountLessThan(
                OfflineSyncQueue.SyncStatus.FAILED, MAX_RETRIES);
        log.info("[SYNC] Retrying {} failed items", failed.size());
        failed.forEach(record -> {
            record.setRetryCount(record.getRetryCount() + 1);
            record.setSyncStatus(OfflineSyncQueue.SyncStatus.PENDING);
            syncRepo.save(record);
        });
    }
}
