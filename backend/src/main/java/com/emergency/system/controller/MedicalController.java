package com.emergency.system.controller;

import com.emergency.system.model.MedicalConsultation;
import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.service.MedicalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Medical and emergency services endpoints.
 *
 * GET  /api/medical/doctors/nearby      — find doctors near a location
 * GET  /api/medical/ambulances/nearby   — find ambulances
 * GET  /api/medical/police/nearby       — find police units
 * POST /api/medical/consultation        — request consultation
 * PATCH /api/medical/consultation/{id}/accept  — doctor accepts
 * PATCH /api/medical/consultation/{id}/end     — end with notes
 */
@RestController
@RequestMapping("/api/medical")
@Tag(name = "Medical Services", description = "Doctor consultations, ambulance dispatch, police")
@RequiredArgsConstructor
public class MedicalController {

    private final MedicalService medicalService;
    private final UserRepository userRepository;

    @Operation(summary = "Find nearby on-duty doctors")
    @GetMapping("/doctors/nearby")
    public ResponseEntity<List<User>> nearbyDoctors(
            @RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(medicalService.findNearbyDoctors(lat, lon));
    }

    @Operation(summary = "Find nearest available ambulances")
    @GetMapping("/ambulances/nearby")
    public ResponseEntity<List<User>> nearbyAmbulances(
            @RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(medicalService.findNearbyAmbulances(lat, lon));
    }

    @Operation(summary = "Find nearest police units")
    @GetMapping("/police/nearby")
    public ResponseEntity<List<User>> nearbyPolice(
            @RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(medicalService.findNearbyPolice(lat, lon));
    }

    @Operation(summary = "Request a video consultation with a doctor")
    @PostMapping("/consultation")
    public ResponseEntity<MedicalConsultation> requestConsultation(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, Object> body) {

        User patient = getUser(principal);
        Long doctorId  = Long.valueOf(body.get("doctorId").toString());
        Long emergencyId = body.containsKey("emergencyId") ?
                Long.valueOf(body.get("emergencyId").toString()) : null;

        return ResponseEntity.ok(
                medicalService.requestConsultation(patient, doctorId, emergencyId));
    }

    @Operation(summary = "Doctor accepts a consultation request")
    @PatchMapping("/consultation/{id}/accept")
    public ResponseEntity<MedicalConsultation> accept(@PathVariable Long id) {
        return ResponseEntity.ok(medicalService.acceptConsultation(id));
    }

    @Operation(summary = "End a consultation and save notes")
    @PatchMapping("/consultation/{id}/end")
    public ResponseEntity<MedicalConsultation> end(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(medicalService.endConsultation(
                id, body.get("notes"), body.get("prescription")));
    }

    @Operation(summary = "Get all pending consultation requests (for doctors)")
    @GetMapping("/consultation/pending")
    public ResponseEntity<List<MedicalConsultation>> pending() {
        return ResponseEntity.ok(medicalService.getPendingConsultations());
    }

    private User getUser(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
