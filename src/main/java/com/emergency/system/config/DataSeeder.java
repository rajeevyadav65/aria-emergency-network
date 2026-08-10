package com.emergency.system.config;

import com.emergency.system.model.DisasterAlert;
import com.emergency.system.model.Emergency;
import com.emergency.system.model.LocationHistory;
import com.emergency.system.model.User;
import com.emergency.system.repository.DisasterAlertRepository;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.LocationHistoryRepository;
import com.emergency.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Seeds demo data on startup so the UI has data to display.
 * Was completely missing from original project.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final EmergencyRepository emergencyRepository;
    private final PasswordEncoder passwordEncoder;
    private final LocationHistoryRepository locationHistoryRepository;
    private final DisasterAlertRepository disasterAlertRepository;

    @Override
    public void run(String... args) {
        seedUsers();
        seedEmergencies();
        seedLocationHistory();
        seedDisasterAlerts();
        log.info("✅ Demo data seeded successfully");
    }

    private void seedUsers() {
        if (!userRepository.existsByEmail("admin@aria.com")) {
            userRepository.save(User.builder()
                    .name("Admin User").email("admin@aria.com")
                    .password(passwordEncoder.encode("admin123"))
                    .deviceId("device-admin-001")
                    .role(User.UserRole.ADMIN)
                    .latitude(27.1767).longitude(78.0081)
                    .build());
        }

        if (!userRepository.existsByEmail("alice@demo.com")) {
            userRepository.save(User.builder()
                    .name("Alice Sharma").email("alice@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .deviceId("device-alice-002")
                    .role(User.UserRole.USER)
                    .latitude(27.1790).longitude(78.0100)
                    .build());
        }

        if (!userRepository.existsByEmail("bob@demo.com")) {
            userRepository.save(User.builder()
                    .name("Bob Verma").email("bob@demo.com")
                    .password(passwordEncoder.encode("demo123"))
                    .deviceId("device-bob-003")
                    .role(User.UserRole.USER)
                    .latitude(27.1750).longitude(78.0060)
                    .build());
        }

        if (!userRepository.existsByDeviceId("guest-device-demo-004")) {
            userRepository.save(User.builder()
                    .deviceId("guest-device-demo-004")
                    .role(User.UserRole.GUEST)
                    .latitude(27.1800).longitude(78.0120)
                    .build());
        }

        // Doctor
        if (!userRepository.existsByEmail("dr.sharma@aria.com")) {
            userRepository.save(User.builder()
                    .name("Dr. Priya Sharma").email("dr.sharma@aria.com")
                    .password(passwordEncoder.encode("doctor123"))
                    .deviceId("device-doctor-005")
                    .role(User.UserRole.DOCTOR)
                    .specialization("Emergency Medicine")
                    .licenseNumber("MCI-12345")
                    .isOnDuty(true).isAvailable(true)
                    .latitude(27.1850).longitude(78.0200)
                    .build());
        }

        // Police
        if (!userRepository.existsByEmail("officer.singh@aria.com")) {
            userRepository.save(User.builder()
                    .name("Officer Raj Singh").email("officer.singh@aria.com")
                    .password(passwordEncoder.encode("police123"))
                    .deviceId("device-police-006")
                    .role(User.UserRole.POLICE)
                    .licenseNumber("UP-POLICE-9876")
                    .isOnDuty(true).isAvailable(true)
                    .latitude(27.1720).longitude(78.0050)
                    .build());
        }

        // Ambulance
        if (!userRepository.existsByEmail("ambulance1@aria.com")) {
            userRepository.save(User.builder()
                    .name("Ambulance Unit 1").email("ambulance1@aria.com")
                    .password(passwordEncoder.encode("amb123"))
                    .deviceId("device-amb-007")
                    .role(User.UserRole.AMBULANCE)
                    .vehicleId("AMB-UP-001")
                    .isOnDuty(true).isAvailable(true)
                    .latitude(27.1900).longitude(78.0300)
                    .build());
        }
        log.info("Seeded demo users including Doctor/Police/Ambulance — admin@aria.com / admin123, alice@demo.com / demo123");
    }

    private void seedEmergencies() {
        if (emergencyRepository.count() > 0) return;

        emergencyRepository.save(Emergency.builder()
                .message("Person fell near Taj Mahal gate, unresponsive")
                .latitude(27.1751).longitude(78.0421)
                .fallDetected(true).movement("STATIONARY").userResponse("NO_RESPONSE")
                .riskLevel(Emergency.RiskLevel.HIGH).aiAction("Possible fall — unresponsive")
                .status(Emergency.EmergencyStatus.ACTIVE)
                .reportedByDeviceId("device-alice-002")
                .createdAt(LocalDateTime.now().minusMinutes(5))
                .build());

        emergencyRepository.save(Emergency.builder()
                .message("Chest pain reported near market area")
                .latitude(27.1990).longitude(78.0081)
                .fallDetected(false).movement("STATIONARY").userResponse("NEED_HELP")
                .riskLevel(Emergency.RiskLevel.HIGH).aiAction("User requested help")
                .status(Emergency.EmergencyStatus.PENDING)
                .reportedByDeviceId("device-bob-003")
                .createdAt(LocalDateTime.now().minusMinutes(12))
                .build());

        emergencyRepository.save(Emergency.builder()
                .message("Minor fall on stairs, user is walking")
                .latitude(27.1830).longitude(78.0140)
                .fallDetected(true).movement("WALKING").userResponse("ARE_YOU_OK")
                .riskLevel(Emergency.RiskLevel.LOW).aiAction("User confirmed safe")
                .status(Emergency.EmergencyStatus.FALSE_ALARM)
                .reportedByDeviceId("guest-device-demo-004")
                .createdAt(LocalDateTime.now().minusMinutes(30))
                .build());

        emergencyRepository.save(Emergency.builder()
                .message("Road accident — two people injured")
                .latitude(27.1650).longitude(77.9950)
                .fallDetected(false).movement("STATIONARY").userResponse("NEED_HELP")
                .riskLevel(Emergency.RiskLevel.HIGH).aiAction("User requested help")
                .status(Emergency.EmergencyStatus.RESOLVED)
                .reportedByDeviceId("device-admin-001")
                .createdAt(LocalDateTime.now().minusHours(2))
                .resolvedAt(LocalDateTime.now().minusHours(1))
                .build());
    }

    private void seedDisasterAlerts() {
        if (disasterAlertRepository.count() > 0) return;
        disasterAlertRepository.save(DisasterAlert.builder()
                .type(DisasterAlert.DisasterType.EARTHQUAKE)
                .title("M4.2 Earthquake — 50km from Agra")
                .description("Minor earthquake detected. Stay calm, check for structural damage.")
                .source("USGS").externalId("demo-eq-001")
                .epicenterLat(27.5).epicenterLon(78.1)
                .magnitude(4.2).radiusKm(100.0)
                .severity(DisasterAlert.DisasterSeverity.LOW)
                .alertStatus(DisasterAlert.AlertStatus.ACTIVE)
                .build());
        log.info("Seeded 1 demo disaster alert");
    }

    private void seedLocationHistory() {
        if (locationHistoryRepository.count() > 0) return;
        double baseLat = 27.1767, baseLon = 78.0081;
        // Alice walks north over the last 30 minutes
        for (int i = 0; i < 8; i++) {
            locationHistoryRepository.save(LocationHistory.builder()
                    .deviceId("device-alice-002")
                    .latitude(baseLat + i * 0.0002)
                    .longitude(baseLon + i * 0.0001)
                    .recordedAt(LocalDateTime.now().minusMinutes(30 - i * 4))
                    .build());
        }
        // Bob stays near market
        for (int i = 0; i < 4; i++) {
            locationHistoryRepository.save(LocationHistory.builder()
                    .deviceId("device-bob-003")
                    .latitude(27.1990 + i * 0.0001)
                    .longitude(78.0081)
                    .recordedAt(LocalDateTime.now().minusMinutes(20 - i * 5))
                    .build());
        }
    }
}
