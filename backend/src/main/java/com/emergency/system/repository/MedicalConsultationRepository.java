package com.emergency.system.repository;

import com.emergency.system.model.MedicalConsultation;
import com.emergency.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicalConsultationRepository extends JpaRepository<MedicalConsultation, Long> {

    List<MedicalConsultation> findByPatientOrderByRequestedAtDesc(User patient);
    List<MedicalConsultation> findByDoctorOrderByRequestedAtDesc(User doctor);

    List<MedicalConsultation> findByDoctorAndStatus(
            User doctor, MedicalConsultation.ConsultationStatus status);

    Optional<MedicalConsultation> findByRoomId(String roomId);

    @Query("SELECT c FROM MedicalConsultation c WHERE c.status = 'REQUESTED' ORDER BY c.requestedAt ASC")
    List<MedicalConsultation> findPendingConsultations();

    long countByDoctorAndStatus(User doctor, MedicalConsultation.ConsultationStatus status);
}
