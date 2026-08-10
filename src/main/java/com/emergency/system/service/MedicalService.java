package com.emergency.system.service;

import com.emergency.system.model.MedicalConsultation;
import com.emergency.system.model.User;
import com.emergency.system.repository.MedicalConsultationRepository;
import com.emergency.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Manages doctor consultations and nearby doctor/ambulance dispatch.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class MedicalService {

    private final MedicalConsultationRepository consultationRepo;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Value("${app.medical.search-radius-km:10}")
    private double searchRadiusKm;

    /**
     * Requests a consultation with the nearest available doctor.
     * Creates a WebRTC room and notifies the doctor.
     */
    public MedicalConsultation requestConsultation(User patient, Long doctorId,
                                                    Long emergencyId) {
        User doctor = userRepository.findById(doctorId)
                .orElseThrow(() -> new IllegalArgumentException("Doctor not found"));

        if (doctor.getRole() != User.UserRole.DOCTOR) {
            throw new IllegalArgumentException("User is not a doctor");
        }

        String roomId = "room-" + UUID.randomUUID().toString().substring(0, 8);
        String token  = UUID.randomUUID().toString();

        MedicalConsultation consultation = consultationRepo.save(
                MedicalConsultation.builder()
                        .patient(patient)
                        .doctor(doctor)
                        .roomId(roomId)
                        .sessionToken(token)
                        .type(MedicalConsultation.ConsultationType.VIDEO)
                        .status(MedicalConsultation.ConsultationStatus.REQUESTED)
                        .build());

        // Notify doctor via WebSocket
        messagingTemplate.convertAndSend(
                "/topic/alerts/" + doctor.getDeviceId(),
                java.util.Map.of(
                        "type", "CONSULTATION_REQUEST",
                        "consultationId", consultation.getId(),
                        "patientName", patient.getName() != null ? patient.getName() : "Patient",
                        "roomId", roomId
                ));

        log.info("[MEDICAL] Consultation {} requested: patient={} doctor={} room={}",
                consultation.getId(), patient.getId(), doctor.getId(), roomId);
        return consultation;
    }

    /** Doctor accepts the consultation */
    public MedicalConsultation acceptConsultation(Long consultationId) {
        MedicalConsultation c = findOrThrow(consultationId);
        c.setStatus(MedicalConsultation.ConsultationStatus.ACTIVE);
        c.setStartedAt(LocalDateTime.now());
        MedicalConsultation saved = consultationRepo.save(c);

        // Notify patient
        messagingTemplate.convertAndSend(
                "/topic/alerts/" + c.getPatient().getDeviceId(),
                java.util.Map.of("type", "CONSULTATION_ACCEPTED",
                        "consultationId", c.getId(), "roomId", c.getRoomId()));
        return saved;
    }

    /** End a consultation and save doctor notes */
    public MedicalConsultation endConsultation(Long consultationId, String notes,
                                                String prescription) {
        MedicalConsultation c = findOrThrow(consultationId);
        c.setStatus(MedicalConsultation.ConsultationStatus.COMPLETED);
        c.setEndedAt(LocalDateTime.now());
        c.setNotes(notes);
        c.setPrescription(prescription);
        return consultationRepo.save(c);
    }

    /** Find nearest on-duty doctors */
    public List<User> findNearbyDoctors(double lat, double lon) {
        return userRepository.findUsersWithinRadiusByRole(
                lat, lon, searchRadiusKm * 1000, User.UserRole.DOCTOR);
    }

    /** Find nearest available ambulances */
    public List<User> findNearbyAmbulances(double lat, double lon) {
        return userRepository.findUsersWithinRadiusByRole(
                lat, lon, searchRadiusKm * 1000, User.UserRole.AMBULANCE);
    }

    /** Find nearest police units */
    public List<User> findNearbyPolice(double lat, double lon) {
        return userRepository.findUsersWithinRadiusByRole(
                lat, lon, searchRadiusKm * 1000, User.UserRole.POLICE);
    }

    public List<MedicalConsultation> getPendingConsultations() {
        return consultationRepo.findPendingConsultations();
    }

    private MedicalConsultation findOrThrow(Long id) {
        return consultationRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultation not found: " + id));
    }
}
