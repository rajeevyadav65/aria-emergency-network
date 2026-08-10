package com.emergency.system.controller;

import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import com.emergency.system.dto.EmergencyDTOs.EmergencyResponse;
import com.emergency.system.model.Emergency;
import com.emergency.system.repository.EmergencyRepository;
import com.emergency.system.service.EmergencyService;
import com.emergency.system.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
@Slf4j
public class EmergencyController {

    private final EmergencyService emergencyService;
    private final NotificationService notificationService;
    private final EmergencyRepository emergencyRepository;

    @PostMapping("/report")
    public ResponseEntity<EmergencyResponse> reportEmergency(@Valid @RequestBody EmergencyRequest request) {
        log.info("Received emergency SOS from device: {}", request.getDeviceId());
        EmergencyResponse response = emergencyService.processEmergency(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<?> getActive() {
        return ResponseEntity.ok(emergencyRepository.findByStatus(Emergency.EmergencyStatus.ACTIVE));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Emergency>> getAll() {
        return ResponseEntity.ok(emergencyRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return emergencyRepository.findById(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<?> resolve(@PathVariable Long id) {
        return emergencyRepository.findById(id)
                .map(emergency -> {
                    emergency.setStatus(Emergency.EmergencyStatus.RESOLVED);
                    if (emergency.getResolvedAt() == null) {
                        emergency.setResolvedAt(LocalDateTime.now());
                    }
                    return ResponseEntity.ok(emergencyRepository.save(emergency));
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
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

    @PostMapping("/ambulance/book")
    public ResponseEntity<String> bookAmbulance(@RequestBody Map<String, Double> locationData) {
        Double lat = locationData.get("latitude");
        Double lon = locationData.get("longitude");

        log.info("Searching for nearest available ambulance at ({}, {})", lat, lon);
        return ResponseEntity.ok("Searching for nearby ambulances... Drivers are being notified.");
    }
}
