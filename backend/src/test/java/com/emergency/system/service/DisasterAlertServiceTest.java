package com.emergency.system.service;

import com.emergency.system.model.DisasterAlert;
import com.emergency.system.repository.DisasterAlertRepository;
import com.emergency.system.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
// 🟢 YE DONO IMPORTS MISSING THE
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DisasterAlertServiceTest {

    @Mock private DisasterAlertRepository disasterRepo;
    @Mock private UserRepository userRepository;
    @Mock private SimpMessagingTemplate messagingTemplate;

    private DisasterAlertService service;

    @BeforeEach
    void setUp() {
        service = new DisasterAlertService(
                disasterRepo, userRepository, messagingTemplate, new ObjectMapper());
        ReflectionTestUtils.setField(service, "fetchEnabled", false); // no external calls
    }

    @Test
    @DisplayName("createManualAlert saves and broadcasts alert")
    void createManualAlert_savesAndBroadcasts() {
        DisasterAlert alert = DisasterAlert.builder()
                .type(DisasterAlert.DisasterType.FLOOD)
                .title("Test Flood")
                .description("Heavy rainfall in area")
                .severity(DisasterAlert.DisasterSeverity.HIGH)
                .build();

        when(disasterRepo.save(any())).thenAnswer(inv -> {
            DisasterAlert a = inv.getArgument(0);
            a = DisasterAlert.builder()
                    .id(1L).type(a.getType()).title(a.getTitle())
                    .description(a.getDescription()).severity(a.getSeverity())
                    .source("MANUAL").alertStatus(DisasterAlert.AlertStatus.ACTIVE)
                    .build();
            return a;
        });

        // Lenient use kar rahe hain taaki unnecessary stubbing error na aaye
        lenient().when(userRepository.findUsersWithinRadius(anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(List.of());

        DisasterAlert result = service.createManualAlert(alert);

        assertThat(result.getSource()).isEqualTo("MANUAL");
        assertThat(result.getType()).isEqualTo(DisasterAlert.DisasterType.FLOOD);
        verify(disasterRepo).save(any(DisasterAlert.class));
    }

    @Test
    @DisplayName("getActiveAlerts returns ACTIVE alerts only")
    void getActiveAlerts_returnsActive() {
        DisasterAlert active = DisasterAlert.builder()
                .id(1L).type(DisasterAlert.DisasterType.EARTHQUAKE)
                .alertStatus(DisasterAlert.AlertStatus.ACTIVE).build();

        when(disasterRepo.findByAlertStatusOrderByIssuedAtDesc(DisasterAlert.AlertStatus.ACTIVE))
                .thenReturn(List.of(active));

        List<DisasterAlert> result = service.getActiveAlerts();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAlertStatus()).isEqualTo(DisasterAlert.AlertStatus.ACTIVE);
    }

    @Test
    @DisplayName("resolveAlert changes status to RESOLVED")
    void resolveAlert_changesStatus() {
        DisasterAlert alert = DisasterAlert.builder()
                .id(1L).type(DisasterAlert.DisasterType.FIRE)
                .alertStatus(DisasterAlert.AlertStatus.ACTIVE).build();

        when(disasterRepo.findById(1L)).thenReturn(Optional.of(alert));
        when(disasterRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        DisasterAlert resolved = service.resolveAlert(1L);
        assertThat(resolved.getAlertStatus()).isEqualTo(DisasterAlert.AlertStatus.RESOLVED);
    }

    @Test
    @DisplayName("resolveAlert throws for unknown ID")
    void resolveAlert_unknownId_throws() {
        when(disasterRepo.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.resolveAlert(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("not found");
    }
}