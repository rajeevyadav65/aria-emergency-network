package com.emergency.system.config;

import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.model.Emergency;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Custom actuator health indicator.
 * Exposed at GET /actuator/health
 *
 * Reports: DB connectivity, active emergency count, system uptime.
 */
@Component("aria")
@RequiredArgsConstructor
public class AriaHealthIndicator implements HealthIndicator {

    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;

    private static final LocalDateTime START_TIME = LocalDateTime.now();

    @Override
    public Health health() {
        try {
            long totalUsers       = userRepository.count();
            long activeEmergencies = emergencyRepository
                    .findByStatus(Emergency.EmergencyStatus.ACTIVE).size();
            long totalEmergencies  = emergencyRepository.count();

            Map<String, Object> details = new LinkedHashMap<>();
            details.put("database", "UP");
            details.put("totalUsers", totalUsers);
            details.put("activeEmergencies", activeEmergencies);
            details.put("totalEmergencies", totalEmergencies);
            details.put("serverStarted", START_TIME.toString());

            return Health.up().withDetails(details).build();

        } catch (Exception ex) {
            return Health.down()
                    .withDetail("database", "UNREACHABLE")
                    .withException(ex)
                    .build();
        }
    }
}
