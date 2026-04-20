package com.emergency.system.service;

import com.emergency.system.model.OfflineSyncQueue;
import com.emergency.system.repository.OfflineSyncQueueRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfflineSyncServiceTest {

    @Mock private OfflineSyncQueueRepository syncRepo;
    @Mock private EmergencyService emergencyService;
    @Mock private LocationService locationService;

    private OfflineSyncService service;

    @BeforeEach
    void setUp() {
        service = new OfflineSyncService(syncRepo, emergencyService, locationService, new ObjectMapper());
    }

    @Test
    @DisplayName("syncBatch with empty items returns zero counts")
    void syncBatch_emptyItems_returnsZero() {
        Map<String, Object> result = service.syncBatch("dev-001", List.of());
        assertThat(result.get("accepted")).isEqualTo(0);
        assertThat(result.get("processed")).isEqualTo(0);
    }

    @Test
    @DisplayName("syncBatch with LOCATION_UPDATE item succeeds")
    void syncBatch_locationUpdate_succeeds() throws Exception {
        Map<String, Object> item = Map.of(
                "localId", "local-uuid-001",
                "type", "LOCATION_UPDATE",
                "payload", Map.of("latitude", 27.17, "longitude", 78.00));

        when(syncRepo.findByLocalId("local-uuid-001")).thenReturn(Optional.empty());
        when(syncRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(locationService.updateUserLocation(any(), anyDouble(), anyDouble())).thenReturn(null);

        Map<String, Object> result = service.syncBatch("dev-001", List.of(item));
        assertThat(result.get("processed")).isEqualTo(1);
        assertThat(result.get("failed")).isEqualTo(0);
    }

    @Test
    @DisplayName("syncBatch skips already-synced items (idempotency)")
    void syncBatch_alreadySynced_skipped() {
        OfflineSyncQueue existing = OfflineSyncQueue.builder()
                .localId("existing-001")
                .syncStatus(OfflineSyncQueue.SyncStatus.SYNCED)
                .build();
        when(syncRepo.findByLocalId("existing-001")).thenReturn(Optional.of(existing));

        Map<String, Object> item = Map.of(
                "localId", "existing-001",
                "type", "LOCATION_UPDATE",
                "payload", Map.of());

        Map<String, Object> result = service.syncBatch("dev-001", List.of(item));
        // Item was already synced — not counted as new
        assertThat(result.get("processed")).isEqualTo(0);
        verify(syncRepo, never()).save(any());
    }

    @Test
    @DisplayName("getPendingCount delegates to repository")
    void getPendingCount_delegatesToRepo() {
        when(syncRepo.countByDeviceIdAndSyncStatus("dev-001", OfflineSyncQueue.SyncStatus.PENDING))
                .thenReturn(5L);
        assertThat(service.getPendingCount("dev-001")).isEqualTo(5L);
    }

    @Test
    @DisplayName("retryFailed resets status to PENDING for retryable items")
    void retryFailed_resetsStatus() {
        OfflineSyncQueue failed = OfflineSyncQueue.builder()
                .localId("fail-001")
                .syncStatus(OfflineSyncQueue.SyncStatus.FAILED)
                .retryCount(1)
                .build();

        when(syncRepo.findBySyncStatusAndRetryCountLessThan(
                eq(OfflineSyncQueue.SyncStatus.FAILED), anyInt()))
                .thenReturn(List.of(failed));
        when(syncRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.retryFailed();

        verify(syncRepo).save(argThat(q -> {
            OfflineSyncQueue item = (OfflineSyncQueue) q;
            return item.getSyncStatus() == OfflineSyncQueue.SyncStatus.PENDING
                    && item.getRetryCount() == 2;
        }));
    }
}
