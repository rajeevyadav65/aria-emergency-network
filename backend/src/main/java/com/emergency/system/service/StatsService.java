package com.emergency.system.service;

import com.emergency.system.model.DisasterAlert;
import com.emergency.system.model.Emergency;
import com.emergency.system.model.MedicalConsultation;
import com.emergency.system.model.OfflineSyncQueue;
import com.emergency.system.repository.*;
import com.emergency.system.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Aggregates system-wide statistics for the admin dashboard.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsService {

    private final EmergencyRepository emergencyRepository;
    private final UserRepository userRepository;
    private final AlertRepository alertRepository;
    private final NotificationLogRepository notificationLogRepository;
    private final DisasterAlertRepository disasterAlertRepository;
    private final MedicalConsultationRepository consultationRepository;
    private final OfflineSyncQueueRepository syncQueueRepository;

    public Map<String, Object> getFullStats() {
        long total = emergencyRepository.count();
        long active = emergencyRepository.findByStatus(Emergency.EmergencyStatus.ACTIVE).size();
        long resolved = emergencyRepository.findByStatus(Emergency.EmergencyStatus.RESOLVED).size();
        long falseAlarms = emergencyRepository.findByStatus(Emergency.EmergencyStatus.FALSE_ALARM).size();
        long pending = emergencyRepository.findByStatus(Emergency.EmergencyStatus.PENDING).size();

        long highRisk = emergencyRepository.findAll().stream()
                .filter(e -> e.getRiskLevel() == Emergency.RiskLevel.HIGH).count();
        long medRisk = emergencyRepository.findAll().stream()
                .filter(e -> e.getRiskLevel() == Emergency.RiskLevel.MEDIUM).count();
        long lowRisk = emergencyRepository.findAll().stream()
                .filter(e -> e.getRiskLevel() == Emergency.RiskLevel.LOW).count();

        long totalUsers = userRepository.count();
        long totalAlerts = alertRepository.count();
        long deliveredNotifs = notificationLogRepository.countByDelivered(true);
        long notifLast24h = notificationLogRepository.countSince(LocalDateTime.now().minusHours(24));

        double resolutionRate = total > 0 ? (double) resolved / total * 100 : 0;
        double falseAlarmRate = total > 0 ? (double) falseAlarms / total * 100 : 0;

        long totalDoctors    = userRepository.findByRole(com.emergency.system.model.User.UserRole.DOCTOR).size();
        long totalPolice     = userRepository.findByRole(com.emergency.system.model.User.UserRole.POLICE).size();
        long totalAmbulances = userRepository.findByRole(com.emergency.system.model.User.UserRole.AMBULANCE).size();
        long activeDisasters = disasterAlertRepository.findByAlertStatusOrderByIssuedAtDesc(DisasterAlert.AlertStatus.ACTIVE).size();
        long pendingConsults = consultationRepository.findPendingConsultations().size();
        long pendingSync     = syncQueueRepository.findBySyncStatusAndRetryCountLessThan(OfflineSyncQueue.SyncStatus.PENDING, 3).size();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalEmergencies", total);
        stats.put("activeEmergencies", active);
        stats.put("resolvedEmergencies", resolved);
        stats.put("falseAlarms", falseAlarms);
        stats.put("pendingEmergencies", pending);
        stats.put("highRiskCount", highRisk);
        stats.put("mediumRiskCount", medRisk);
        stats.put("lowRiskCount", lowRisk);
        stats.put("totalUsers", totalUsers);
        stats.put("totalAlerts", totalAlerts);
        stats.put("deliveredNotifications", deliveredNotifs);
        stats.put("notificationsLast24h", notifLast24h);
        stats.put("resolutionRatePercent", Math.round(resolutionRate * 10.0) / 10.0);
        stats.put("falseAlarmRatePercent", Math.round(falseAlarmRate * 10.0) / 10.0);
        stats.put("totalDoctors", totalDoctors);
        stats.put("totalPolice", totalPolice);
        stats.put("totalAmbulances", totalAmbulances);
        stats.put("activeDisasterAlerts", activeDisasters);
        stats.put("pendingConsultations", pendingConsults);
        stats.put("pendingOfflineSync", pendingSync);
        return stats;
    }
}
