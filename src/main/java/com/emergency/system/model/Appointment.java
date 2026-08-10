package com.emergency.system.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Appointment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long patientId;
    private Long doctorId;
    private String patientName;

    // 🟢 Payment Info
    private String paymentId;
    private Double amountPaid;

    // 🟢 Queue Management
    private Integer queueNumber;
    private String status; // PENDING, IN_PROGRESS, COMPLETED

    // 🟢 Document/Report URL (For user to show PDF/Photo in Video Call)
    private String reportFileUrl;

    @Builder.Default
    private LocalDateTime bookingTime = LocalDateTime.now();
}