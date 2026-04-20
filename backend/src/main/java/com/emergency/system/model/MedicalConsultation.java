package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Medical consultation session (video/voice call between USER and DOCTOR).
 */
@Entity
@Table(name = "medical_consultations")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MedicalConsultation {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private User patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "emergency_id")
    private Emergency emergency;   // Optional — linked to an emergency

    private String roomId;         // WebRTC room ID
    private String sessionToken;   // Secure token for this session

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ConsultationType type = ConsultationType.VIDEO;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ConsultationStatus status = ConsultationStatus.REQUESTED;

    @Column(length = 2000)
    private String notes;          // Doctor's notes
    @Column(length = 500)
    private String prescription;

    @Builder.Default
    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    public enum ConsultationType { VIDEO, VOICE, CHAT }

    public enum ConsultationStatus {
        REQUESTED, ACCEPTED, ACTIVE, COMPLETED, REJECTED, CANCELLED
    }
}
