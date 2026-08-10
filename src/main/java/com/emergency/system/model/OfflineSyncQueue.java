package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Queue for data created in OFFLINE mode (Bluetooth/WiFi Direct).
 * Synced to server when internet is restored.
 */
@Entity
@Table(name = "offline_sync_queue")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OfflineSyncQueue {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SyncType type;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String payload;         // JSON of the offline record

    @Column(nullable = false)
    private String localId;         // UUID assigned offline

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SyncStatus syncStatus = SyncStatus.PENDING;

    private String serverAssignedId;  // ID from server after sync
    private String errorMessage;
    private int retryCount;

    @Builder.Default
    private LocalDateTime createdOfflineAt = LocalDateTime.now();

    private LocalDateTime syncedAt;

    public enum SyncType {
        EMERGENCY_REPORT,
        LOCATION_UPDATE,
        CHAT_MESSAGE,
        ALERT_ACKNOWLEDGEMENT,
        USER_PROFILE_UPDATE
    }

    public enum SyncStatus {
        PENDING, SYNCING, SYNCED, FAILED, CONFLICT
    }
}
