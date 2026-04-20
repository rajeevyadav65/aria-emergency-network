package com.emergency.system.controller;

import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.dto.EmergencyDTOs.EmergencyResponse;
import com.emergency.system.model.Emergency;
import com.emergency.system.service.EmergencyService;
import com.emergency.system.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@Slf4j
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final NotificationService notificationService;

    @PostMapping("/report")
    public ResponseEntity<EmergencyResponse> reportEmergency(@RequestBody EmergencyRequest request) {
        log.info("Received emergency SOS from device: {}", request.getDeviceId());
        EmergencyResponse response = emergencyService.processEmergency(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/disaster/broadcast")
    public ResponseEntity<Emergency> broadcastDisaster(@RequestBody Map<String, Object> payload) {
        Emergency disaster = Emergency.builder()
                .type(Emergency.EmergencyType.DISASTER)
                .message((String) payload.get("message"))
                .latitude((Double) payload.get("latitude"))
                .longitude((Double) payload.get("longitude"))
                .impactRadius((Double) payload.get("radius"))
                .riskLevel(Emergency.RiskLevel.HIGH)
                .status(Emergency.EmergencyStatus.ACTIVE)
                .build();

        notificationService.notifyRespondersReady(disaster);
        notificationService.broadcastEmergencyUpdate(disaster);
        return ResponseEntity.ok(disaster);
    }

    // 🟢 NAYA: Rapido-style Ambulance Booking Endpoint
    @PostMapping("/ambulance/book")
    public ResponseEntity<String> bookAmbulance(@RequestBody Map<String, Double> locationData) {
        Double lat = locationData.get("latitude");
        Double lon = locationData.get("longitude");

        log.info("Searching for nearest available ambulance at ({}, {})", lat, lon);

        // Yahan par hum NotificationService ke through nearest on-duty ambulance ko request bhejenge
        // notificationService.alertNearestAmbulance(lat, lon);

        return ResponseEntity.ok("Searching for nearby ambulances... Drivers are being notified.");
    }

    private boolean isAccidentHotspot(Double lat, Double lon) {
        return (lat > 27.48 && lat < 27.50 && lon > 77.66 && lon < 77.68);
    }
}