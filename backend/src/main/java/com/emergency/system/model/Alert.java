package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Alert record — one per (Emergency × User) pair.
 * NOTE: Original code had this class inside Emergency.java — FIXED here.
 */
@Entity
@Table(name = "alerts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_id", nullable = false)
    private Emergency emergency;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(length = 1000)
    private String message;

    private Double distanceMeters;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AlertStatus status = AlertStatus.SENT;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now();

    public enum AlertStatus {
        SENT, ACKNOWLEDGED, RESPONDED
    }
}
