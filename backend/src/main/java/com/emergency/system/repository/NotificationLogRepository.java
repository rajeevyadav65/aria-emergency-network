package com.emergency.system.repository;

import com.emergency.system.model.NotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for NotificationLog entity.
 * Handles data access for audit logs and notification history.
 */
@Repository
public interface NotificationLogRepository extends JpaRepository<NotificationLog, Long> {

    // Emergency ID ke basis par logs dhoondhne ke liye
    List<NotificationLog> findByEmergencyId(Long emergencyId);

    // FIX: recipientDeviceId ko badal kar deviceId kar diya gaya hai (Model ke hisab se)
    List<NotificationLog> findByDeviceId(String deviceId);

    // Delivery status ke basis par count karne ke liye
    long countByDelivered(boolean delivered);

    // Custom query: Kisi specific time ke baad kitne notifications gaye
    @Query("SELECT COUNT(n) FROM NotificationLog n WHERE n.sentAt >= :since")
    long countSince(@Param("since") LocalDateTime since);
}