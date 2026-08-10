package com.emergency.system.service;

import com.emergency.system.model.MedicalConsultation;
import com.emergency.system.model.User;
import com.emergency.system.repository.MedicalConsultationRepository;
import com.emergency.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MedicalServiceTest {

    @Mock private MedicalConsultationRepository consultationRepo;
    @Mock private UserRepository userRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    private MedicalService service;

    private User patient;
    private User doctor;

    @BeforeEach
    void setUp() {
        service = new MedicalService(consultationRepo, userRepository, messagingTemplate);
        ReflectionTestUtils.setField(service, "searchRadiusKm", 10.0);

        patient = User.builder().id(1L).name("Alice").deviceId("dev-alice")
                .role(User.UserRole.USER).build();
        doctor = User.builder().id(2L).name("Dr. Smith").deviceId("dev-doctor")
                .role(User.UserRole.DOCTOR).specialization("Emergency Medicine").build();
    }

    @Test
    @DisplayName("requestConsultation creates consultation and notifies doctor")
    void requestConsultation_success() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(doctor));
        when(consultationRepo.save(any())).thenAnswer(inv -> {
            MedicalConsultation c = inv.getArgument(0);
            return MedicalConsultation.builder()
                    .id(10L).patient(c.getPatient()).doctor(c.getDoctor())
                    .roomId(c.getRoomId()).status(MedicalConsultation.ConsultationStatus.REQUESTED)
                    .build();
        });

        MedicalConsultation result = service.requestConsultation(patient, 2L, null);

        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getStatus()).isEqualTo(MedicalConsultation.ConsultationStatus.REQUESTED);
        verify(messagingTemplate).convertAndSend(
                eq("/topic/alerts/dev-doctor"), any(java.util.Map.class));
    }

    @Test
    @DisplayName("requestConsultation throws when user is not DOCTOR role")
    void requestConsultation_notDoctor_throws() {
        User nonDoctor = User.builder().id(3L).role(User.UserRole.USER).build();
        when(userRepository.findById(3L)).thenReturn(Optional.of(nonDoctor));

        assertThatThrownBy(() -> service.requestConsultation(patient, 3L, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not a doctor");
    }

    @Test
    @DisplayName("acceptConsultation sets status to ACTIVE and notifies patient")
    void acceptConsultation_setsActive() {
        MedicalConsultation consultation = MedicalConsultation.builder()
                .id(10L).patient(patient).doctor(doctor)
                .roomId("room-abc").status(MedicalConsultation.ConsultationStatus.REQUESTED).build();

        when(consultationRepo.findById(10L)).thenReturn(Optional.of(consultation));
        when(consultationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MedicalConsultation result = service.acceptConsultation(10L);

        assertThat(result.getStatus()).isEqualTo(MedicalConsultation.ConsultationStatus.ACTIVE);
        assertThat(result.getStartedAt()).isNotNull();
        verify(messagingTemplate).convertAndSend(
                eq("/topic/alerts/dev-alice"), any(java.util.Map.class));
    }

    @Test
    @DisplayName("endConsultation sets status COMPLETED with notes")
    void endConsultation_setsCompleted() {
        MedicalConsultation consultation = MedicalConsultation.builder()
                .id(10L).patient(patient).doctor(doctor)
                .status(MedicalConsultation.ConsultationStatus.ACTIVE).build();

        when(consultationRepo.findById(10L)).thenReturn(Optional.of(consultation));
        when(consultationRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        MedicalConsultation result = service.endConsultation(10L, "Patient is stable", "Rest for 2 days");

        assertThat(result.getStatus()).isEqualTo(MedicalConsultation.ConsultationStatus.COMPLETED);
        assertThat(result.getNotes()).isEqualTo("Patient is stable");
        assertThat(result.getPrescription()).isEqualTo("Rest for 2 days");
        assertThat(result.getEndedAt()).isNotNull();
    }

    @Test
    @DisplayName("findNearbyDoctors delegates to userRepository")
    void findNearbyDoctors_delegatesToRepo() {
        when(userRepository.findUsersWithinRadiusByRole(
                anyDouble(), anyDouble(), anyDouble(), eq(User.UserRole.DOCTOR)))
                .thenReturn(List.of(doctor));

        List<User> result = service.findNearbyDoctors(27.1767, 78.0081);
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRole()).isEqualTo(User.UserRole.DOCTOR);
    }

    @Test
    @DisplayName("findNearbyAmbulances returns ambulance-role users")
    void findNearbyAmbulances() {
        User ambulance = User.builder().id(5L).role(User.UserRole.AMBULANCE).vehicleId("AMB-001").build();
        when(userRepository.findUsersWithinRadiusByRole(
                anyDouble(), anyDouble(), anyDouble(), eq(User.UserRole.AMBULANCE)))
                .thenReturn(List.of(ambulance));

        List<User> result = service.findNearbyAmbulances(27.1767, 78.0081);
        assertThat(result).hasSize(1);
    }
}
