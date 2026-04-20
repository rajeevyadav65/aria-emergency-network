package com.emergency.system;

import com.emergency.system.controller.*;
import com.emergency.system.controller.DisasterAlertController;
import com.emergency.system.controller.MedicalController;
import com.emergency.system.controller.SyncController;
import com.emergency.system.controller.VoiceKeywordController;
import com.emergency.system.controller.RoleDispatchController;
import com.emergency.system.controller.AiDetectionController;
import com.emergency.system.controller.EmergencyAnalyticsController;
import com.emergency.system.controller.LocationShareController;
import com.emergency.system.service.AiDetectionService;
import com.emergency.system.service.EmergencyAnalyticsService;
import com.emergency.system.service.*;
import com.emergency.system.service.DisasterAlertService;
import com.emergency.system.service.MedicalService;
import com.emergency.system.service.OfflineSyncService;
import com.emergency.system.service.VoiceKeywordService;
import com.emergency.system.repository.*;
import com.emergency.system.repository.DisasterAlertRepository;
import com.emergency.system.repository.MedicalConsultationRepository;
import com.emergency.system.repository.OfflineSyncQueueRepository;
import com.emergency.system.repository.VoiceKeywordRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Full Spring application context smoke test.
 * Verifies the entire application wires up correctly.
 * If this passes, all beans load, dependencies resolve, and properties bind.
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Application Context Smoke Test")
class EmergencyApplicationTest {

    @Autowired private ApplicationContext context;

    // New controllers
    @Autowired private DisasterAlertController disasterController;
    @Autowired private MedicalController medicalController;
    @Autowired private SyncController syncController;
    @Autowired private VoiceKeywordController voiceController;
    @Autowired private RoleDispatchController dispatchController;
    @Autowired private AiDetectionController detectionController;
    @Autowired private EmergencyAnalyticsController analyticsController;
    @Autowired private LocationShareController locationShareController;

    // New services
    @Autowired private DisasterAlertService disasterAlertService;
    @Autowired private MedicalService medicalService;
    @Autowired private OfflineSyncService offlineSyncService;
    @Autowired private VoiceKeywordService voiceKeywordService;
    @Autowired private AiDetectionService aiDetectionService;
    @Autowired private EmergencyAnalyticsService analyticsService;

    // New repositories
    @Autowired private DisasterAlertRepository disasterAlertRepository;
    @Autowired private MedicalConsultationRepository consultationRepository;
    @Autowired private OfflineSyncQueueRepository syncQueueRepository;
    @Autowired private VoiceKeywordRepository voiceKeywordRepository;

        // Controllers
    @Autowired private AuthController authController;
    @Autowired private EmergencyController emergencyController;
    @Autowired private ChatController chatController;
    @Autowired private AlertController alertController;
    @Autowired private AdminController adminController;
    @Autowired private EmergencySearchController searchController;
    @Autowired private LocationUserController locationController;
    @Autowired private UserProfileController profileController;

    // Services
    @Autowired private EmergencyService emergencyService;
    @Autowired private AiAnalysisService aiAnalysisService;
    @Autowired private AiChatService aiChatService;
    @Autowired private LocationService locationService;
    @Autowired private NotificationService notificationService;
    @Autowired private UserService userService;
    @Autowired private StatsService statsService;
    @Autowired private EmergencySearchService searchService;

    // Repositories
    @Autowired private EmergencyRepository emergencyRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AlertRepository alertRepository;
    @Autowired private ChatMessageRepository chatMessageRepository;
    @Autowired private NotificationLogRepository notificationLogRepository;
    @Autowired private LocationHistoryRepository locationHistoryRepository;

    @Test
    @DisplayName("Application context loads without errors")
    void contextLoads() {
        assertThat(context).isNotNull();
    }

    @Test
    @DisplayName("All controllers are present in context")
    void allControllersPresent() {
        assertThat(authController).isNotNull();
        assertThat(emergencyController).isNotNull();
        assertThat(chatController).isNotNull();
        assertThat(alertController).isNotNull();
        assertThat(adminController).isNotNull();
        assertThat(searchController).isNotNull();
        assertThat(locationController).isNotNull();
        assertThat(profileController).isNotNull();
    }

    @Test
    @DisplayName("All new controllers are wired up")
    void newControllersPresent() {
        assertThat(disasterController).isNotNull();
        assertThat(medicalController).isNotNull();
        assertThat(syncController).isNotNull();
        assertThat(voiceController).isNotNull();
        assertThat(dispatchController).isNotNull();
        assertThat(detectionController).isNotNull();
        assertThat(analyticsController).isNotNull();
        assertThat(locationShareController).isNotNull();
    }

    @Test
    @DisplayName("All new services are present in context")
    void newServicesPresent() {
        assertThat(disasterAlertService).isNotNull();
        assertThat(medicalService).isNotNull();
        assertThat(offlineSyncService).isNotNull();
        assertThat(voiceKeywordService).isNotNull();
    }

    @Test
    @DisplayName("All services are present in context")
    void allServicesPresent() {
        assertThat(emergencyService).isNotNull();
        assertThat(aiAnalysisService).isNotNull();
        assertThat(aiChatService).isNotNull();
        assertThat(locationService).isNotNull();
        assertThat(notificationService).isNotNull();
        assertThat(userService).isNotNull();
        assertThat(statsService).isNotNull();
        assertThat(searchService).isNotNull();
    }

    @Test
    @DisplayName("All repositories are present in context")
    void allRepositoriesPresent() {
        assertThat(emergencyRepository).isNotNull();
        assertThat(userRepository).isNotNull();
        assertThat(alertRepository).isNotNull();
        assertThat(chatMessageRepository).isNotNull();
        assertThat(notificationLogRepository).isNotNull();
        assertThat(locationHistoryRepository).isNotNull();
    }

    @Test
    @DisplayName("Demo data is seeded on startup")
    void demoDataSeeded() {
        assertThat(userRepository.count()).isGreaterThanOrEqualTo(3);
        assertThat(emergencyRepository.count()).isGreaterThanOrEqualTo(3);
    }

    @Test
    @DisplayName("Demo responder users are seeded")
    void responderUsersSeeded() {
        assertThat(userRepository.findByEmail("dr.sharma@aria.com")).isPresent();
        assertThat(userRepository.findByEmail("officer.singh@aria.com")).isPresent();
        assertThat(userRepository.findByEmail("ambulance1@aria.com")).isPresent();
    }

    @Test
    @DisplayName("UserRepository can find seeded demo user")
    void canFindDemoUser() {
        assertThat(userRepository.findByEmail("alice@demo.com")).isPresent();
        assertThat(userRepository.findByEmail("admin@aria.com")).isPresent();
    }
}
