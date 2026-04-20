package com.emergency.system.repository;

import com.emergency.system.model.LocationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LocationHistoryRepository extends JpaRepository<LocationHistory, Long> {

    /** Last N positions for a device, newest first */
    List<LocationHistory> findByDeviceIdOrderByRecordedAtDesc(String deviceId);
    java.util.Optional<LocationHistory> findTopByDeviceIdOrderByRecordedAtDesc(String deviceId);

    /** Positions within a time window */
    List<LocationHistory> findByDeviceIdAndRecordedAtBetweenOrderByRecordedAtAsc(
            String deviceId, LocalDateTime from, LocalDateTime to);

    /** Prune old records (keep last 24h) */
    @Modifying
    @Transactional
    @Query("DELETE FROM LocationHistory l WHERE l.recordedAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
