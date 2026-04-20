package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Records every GPS location update for a device.
 * Enables movement history, trail playback on map, and forensics after an emergency.
 */
@Entity
@Table(name = "location_history",
        indexes = @Index(columnList = "deviceId, recordedAt"))
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class LocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /** Accuracy in metres (from device GPS, optional) */
    private Double accuracyMeters;

    /** Speed in m/s (from device, optional) */
    private Double speedMps;

    @Builder.Default
    private LocalDateTime recordedAt = LocalDateTime.now();
}
