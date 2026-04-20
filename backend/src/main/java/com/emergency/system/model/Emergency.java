package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "emergencies")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Emergency {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000)
    private String message;

    private Double latitude;
    private Double longitude;

    // 🟢 RESTORED: Old fields required by EmergencyService and DataSeeder
    private Boolean fallDetected;
    private String movement;
    private String userResponse;
    private LocalDateTime resolvedAt;

    private String reportedByDeviceId;

    // 🟢 NEW: Disaster and Hotspot features
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EmergencyType type = EmergencyType.PERSONAL;

    private Double impactRadius;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(length = 500)
    private String aiAction;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EmergencyStatus status = EmergencyStatus.PENDING;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EmergencyType { PERSONAL, DISASTER, ACCIDENT_HOTSPOT }
    public enum RiskLevel { LOW, MEDIUM, HIGH, FALSE_ALARM }
    public enum EmergencyStatus { PENDING, ACTIVE, RESOLVED, FALSE_ALARM }
}