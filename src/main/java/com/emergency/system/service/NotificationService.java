package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.AlertNotification;
import com.emergency.system.model.Alert;
import com.emergency.system.model.Emergency;
import com.emergency.system.model.NotificationLog;
import com.emergency.system.model.User;
import com.emergency.system.repository.NotificationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationLogRepository notificationLogRepository;

    // 🟢 RESTORED: Old method required by EmergencyService
    @Async
    public void sendAlertToUser(Alert alert, Emergency emergency, User recipient) {
        String msg = buildAlertMessage(emergency);
        AlertNotification notification = AlertNotification.builder()
                .alertId(alert.getId()).emergencyId(emergency.getId())
                .riskLevel(emergency.getRiskLevel()).message(msg)
                .emergencyLatitude(emergency.getLatitude())
                .emergencyLongitude(emergency.getLongitude())
                .distanceMeters(alert.getDistanceMeters())
                .sentAt(alert.getSentAt() != null ? alert.getSentAt() : LocalDateTime.now())
                .build();

        String destination = "/topic/alerts/" + recipient.getDeviceId();
        boolean delivered = false; String failureReason = null;
        try {
            messagingTemplate.convertAndSend(destination, notification);
            delivered = true;
        } catch (Exception e) {
            failureReason = e.getMessage();
        }

        notificationLogRepository.save(NotificationLog.builder()
                .type(NotificationLog.NotificationType.EMERGENCY_ALERT)
                .emergencyId(emergency.getId()).deviceId(recipient.getDeviceId())
                .channel("WEBSOCKET").message(msg).delivered(delivered).failureReason(failureReason).build());
    }

    // 🟢 RESTORED: Old method required by EmergencyService
    @Async
    public void broadcastHighRiskEmergency(Emergency emergency, int nearbyCount) {
        String msg = buildAlertMessage(emergency);
        boolean delivered = false; String failureReason = null;
        try {
            messagingTemplate.convertAndSend("/topic/emergency/broadcast", msg);
            delivered = true;
        } catch (Exception e) {
            failureReason = e.getMessage();
        }

        notificationLogRepository.save(NotificationLog.builder()
                .type(NotificationLog.NotificationType.HIGH_RISK_BROADCAST)
                .emergencyId(emergency.getId()).deviceId("BROADCAST")
                .channel("WEBSOCKET").message(msg).delivered(delivered).failureReason(failureReason).build());
    }

    private String buildAlertMessage(Emergency emergency) {
        return String.format("[%s] Emergency nearby: %s", emergency.getRiskLevel(),
                emergency.getMessage() != null ? emergency.getMessage() : "Emergency detected");
    }

    // 🟢 NEW: Broadcast Disaster to UI Heatmap
    @Async
    public void broadcastDisasterAlert(Emergency disaster) {
        try {
            messagingTemplate.convertAndSend("/topic/disasters", disaster);
        } catch (Exception e) {
            log.error("Failed to broadcast disaster: {}", e.getMessage());
        }
    }

    // 🟢 NEW: Standby Alert for Hospitals & Ambulances
    @Async
    public void notifyRespondersReady(Emergency disaster) {
        String alertMsg = "🚨 STANDBY ALERT: " + disaster.getMessage() +
                ". Area: (" + disaster.getLatitude() + "," + disaster.getLongitude() + "). Please be ready.";
        messagingTemplate.convertAndSend("/topic/responders/readiness", alertMsg);
    }

    // 🟢 NEW: General Broadcast
    public void broadcastEmergencyUpdate(Emergency e) {
        messagingTemplate.convertAndSend("/topic/emergencies", e);
    }
}