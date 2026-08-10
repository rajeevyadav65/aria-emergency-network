package com.emergency.system.service;

import com.emergency.system.config.EmergencyEventListener.EmergencyEventPublisher;
import com.emergency.system.repository.AlertRepository;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class EmergencyServiceTest {

    private EmergencyService emergencyService;

    @Mock private AiAnalysisService aiAnalysisService;
    @Mock private LocationService locationService;
    @Mock private NotificationService notificationService;
    @Mock private EmergencyRepository emergencyRepository;
    @Mock private AlertRepository alertRepository;
    @Mock private UserRepository userRepository;
    @Mock private EmergencyEventPublisher eventPublisher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // 🟢 FIXED: Matching the 7 arguments in EmergencyService constructor
        emergencyService = new EmergencyService(
                aiAnalysisService,
                locationService,
                notificationService,
                emergencyRepository,
                alertRepository,
                userRepository,
                eventPublisher
        );
    }
}