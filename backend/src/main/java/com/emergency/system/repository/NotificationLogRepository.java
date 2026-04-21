package com.emergency.system.repository;

import com.emergency.system.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // Custom methods
    List<NotificationLog> findByDeviceId(String deviceId);
    long countByDelivered(boolean delivered);
    List<NotificationLog> findByEmergencyId(Long emergencyId);
    long countBySentAtGreaterThanEqual(LocalDateTime since);

    default long countSince(LocalDateTime since) {
        return countBySentAtGreaterThanEqual(since);
    }
}
