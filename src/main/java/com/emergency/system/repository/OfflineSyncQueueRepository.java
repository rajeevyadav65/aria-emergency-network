package com.emergency.system.repository;

import com.emergency.system.model.OfflineSyncQueue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OfflineSyncQueueRepository extends JpaRepository<OfflineSyncQueue, Long> {

    List<OfflineSyncQueue> findByDeviceIdAndSyncStatusOrderByCreatedOfflineAtAsc(
            String deviceId, OfflineSyncQueue.SyncStatus status);

    List<OfflineSyncQueue> findBySyncStatusAndRetryCountLessThan(
            OfflineSyncQueue.SyncStatus status, int maxRetries);

    Optional<OfflineSyncQueue> findByLocalId(String localId);

    long countByDeviceIdAndSyncStatus(String deviceId, OfflineSyncQueue.SyncStatus status);
}
