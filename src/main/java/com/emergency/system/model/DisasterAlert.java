package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * System-wide disaster alerts (earthquake, flood, fire, etc.)
 * Broadcast to ALL users in affected area.
 */
@Entity
@Table(name = "disaster_alerts")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DisasterAlert {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DisasterType type;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    private String source;          // "USGS", "IMD", "NDMA", "MANUAL"
    private String externalId;      // ID from external API

    private Double epicenterLat;
    private Double epicenterLon;
    private Double radiusKm;        // Affected radius
    private Double magnitude;       // For earthquakes

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private DisasterSeverity severity = DisasterSeverity.MEDIUM;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertStatus alertStatus = AlertStatus.ACTIVE;

    private int usersNotified;

    @Builder.Default
    private LocalDateTime issuedAt = LocalDateTime.now();
    private LocalDateTime expiresAt;

    public enum DisasterType {
        EARTHQUAKE, FLOOD, FIRE, CYCLONE, TSUNAMI, LANDSLIDE,
        INDUSTRIAL_ACCIDENT, TERRORIST_ATTACK, PANDEMIC, OTHER
    }

    public enum DisasterSeverity {
        LOW, MEDIUM, HIGH, CRITICAL
    }

    public enum AlertStatus {
        ACTIVE, RESOLVED, CANCELLED, EXPIRED
    }
}
