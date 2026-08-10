package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone; // 🟢 NAYA: Phone Number Field

    private String password;

    @Column(nullable = false)
    private String deviceId;

    private Double latitude;
    private Double longitude;
    private String fcmToken;
    private String licenseNumber;
    private String specialization;
    private String vehicleId;
    private Boolean isOnDuty;
    private Boolean isAvailable;

    @Builder.Default
    private String preferredLanguage = "en";

    @Builder.Default
    private Double consultationFee = 0.0;
    private String upiId;
    private String bankAccountNumber;
    private String ifscCode;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserRole role = UserRole.GUEST;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime locationUpdatedAt;

    public enum UserRole {
        GUEST, USER, DOCTOR, POLICE, AMBULANCE, ADMIN
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, ON_DUTY, OFF_DUTY
    }
}