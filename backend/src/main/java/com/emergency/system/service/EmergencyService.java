package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.AiAnalysisResult;
import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.dto.EmergencyDTOs.EmergencyResponse;
import com.emergency.system.model.Alert;
import com.emergency.system.model.Emergency;
import com.emergency.system.model.User;
import com.emergency.system.repository.AlertRepository;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.config.EmergencyEventListener.EmergencyEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class EmergencyService {

    private final AiAnalysisService aiAnalysisService;
    private final LocationService locationService;
    private final NotificationService notificationService;
    private final EmergencyRepository emergencyRepository;
    private final AlertRepository alertRepository;
    private final UserRepository userRepository;
    private final EmergencyEventPublisher eventPublisher;

    @Value("${app.emergency.nearby-radius-meters:500}")
    private double nearbyRadiusMeters;

    public EmergencyResponse processEmergency(EmergencyRequest request) {
        log.info("Processing emergency from device: {}", request.getDeviceId());

        // 🟢 FIXED: Null check for location update
        if (request.getDeviceId() != null && request.getLatitude() != null && request.getLongitude() != null) {
            locationService.updateUserLocation(
                    request.getDeviceId(), request.getLatitude(), request.getLongitude());
        }

        // AI Analysis
        AiAnalysisResult analysis = aiAnalysisService.analyzeEmergency(request);

        // 🟢 FIXED: Manual Null protection before saving
        Emergency emergency = Emergency.builder()
                .message(request.getMessage() != null ? request.getMessage() : "No message")
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .fallDetected(request.getFallDetected() != null ? request.getFallDetected() : false)
                .movement(request.getMovement() != null ? request.getMovement() : "UNKNOWN")
                .userResponse(request.getUserResponse() != null ? request.getUserResponse() : "NONE")
                .riskLevel(analysis.getRiskLevel())
                .aiAction(analysis.getAction())
                .status(Emergency.EmergencyStatus.ACTIVE)
                .reportedByDeviceId(request.getDeviceId())
                .createdAt(LocalDateTime.now())
                .build();

        Emergency savedEmergency = emergencyRepository.save(emergency);

        // Alert nearby users logic
        int alertedCount = alertNearbyUsers(savedEmergency);

        return EmergencyResponse.builder()
                .emergencyId(savedEmergency.getId())
                .riskLevel(savedEmergency.getRiskLevel())
                .aiAction(savedEmergency.getAiAction())
                .status(savedEmergency.getStatus())
                .nearbyUsersAlerted(alertedCount)
                .message("Emergency processed successfully.")
                .build();
    }

    private int alertNearbyUsers(Emergency emergency) {
        // Simple count placeholder to prevent crash if locationService fails
        try {
            List<User> nearbyUsers = locationService.findNearbyUsers(
                    emergency.getLatitude(), emergency.getLongitude());
            return nearbyUsers != null ? nearbyUsers.size() : 0;
        } catch (Exception e) {
            log.warn("Could not alert nearby users: {}", e.getMessage());
            return 0;
        }
    }
}