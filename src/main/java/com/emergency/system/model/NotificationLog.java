package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Audit log for every notification dispatched by the system.
 * Enables analytics, replay, and debugging.
 */
@Entity
@Table(name = "notification_logs")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private Long emergencyId;

    // FIX 1: 'recipientDeviceId' ko 'deviceId' mein change kiya gaya hai
    private String deviceId;

    private String channel;        // WEBSOCKET, SMS, EMAIL

    @Column(length = 1000)
    private String message;

    private boolean delivered;
    private String failureReason;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    // FIX 2: 'deliveredAt' naya variable add kiya gaya hai
    private LocalDateTime deliveredAt;

    public enum NotificationType {
        EMERGENCY_ALERT, HIGH_RISK_BROADCAST, SYSTEM_NOTIFICATION
    }
}