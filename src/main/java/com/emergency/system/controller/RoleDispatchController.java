package com.emergency.system.controller;

import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Role-specific dispatch endpoints for DOCTOR, POLICE, AMBULANCE.
 *
 * PATCH /api/dispatch/duty          — toggle on/off duty
 * GET   /api/dispatch/doctors       — all on-duty doctors
 * GET   /api/dispatch/police        — all on-duty police
 * GET   /api/dispatch/ambulances    — all available ambulances
 * POST  /api/dispatch/respond       — respond to an emergency (any responder role)
 */
@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Role Dispatch", description = "Duty status and emergency dispatch for responder roles")
@RequiredArgsConstructor
public class RoleDispatchController {

    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Operation(summary = "Toggle on/off duty status for responder roles")
    @PatchMapping("/duty")
    public ResponseEntity<Map<String, Object>> toggleDuty(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, Object> body) {

        User user = getUser(principal);
        boolean onDuty = Boolean.TRUE.equals(body.get("onDuty"));
        boolean available = body.containsKey("isAvailable")
                ? Boolean.TRUE.equals(body.get("isAvailable")) : onDuty;

        user.setIsOnDuty(onDuty);
        user.setIsAvailable(available);
        if (body.containsKey("fcmToken")) user.setFcmToken((String) body.get("fcmToken"));

        userRepository.save(user);
        return ResponseEntity.ok(Map.of(
                "userId", user.getId(),
                "role", user.getRole(),
                "onDuty", onDuty,
                "isAvailable", available
        ));
    }

    @Operation(summary = "Get all on-duty doctors")
    @GetMapping("/doctors")
    public ResponseEntity<List<User>> onDutyDoctors() {
        return ResponseEntity.ok(
                userRepository.findByRoleAndIsOnDuty(User.UserRole.DOCTOR, true));
    }

    @Operation(summary = "Get all on-duty police units")
    @GetMapping("/police")
    public ResponseEntity<List<User>> onDutyPolice() {
        return ResponseEntity.ok(
                userRepository.findByRoleAndIsOnDuty(User.UserRole.POLICE, true));
    }

    @Operation(summary = "Get all available ambulances")
    @GetMapping("/ambulances")
    public ResponseEntity<List<User>> availableAmbulances() {
        return ResponseEntity.ok(
                userRepository.findByRoleAndIsAvailable(User.UserRole.AMBULANCE, true));
    }

    /**
     * Responder acknowledges and responds to an emergency.
     * Broadcasts their ETA and current location to the victim.
     */
    @Operation(summary = "Responder accepts an emergency dispatch")
    @PostMapping("/respond")
    public ResponseEntity<Map<String, Object>> respond(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, Object> body) {

        User responder = getUser(principal);
        Long emergencyId = Long.valueOf(body.get("emergencyId").toString());
        String eta = (String) body.getOrDefault("etaMinutes", "unknown");
        String victimDeviceId = (String) body.get("victimDeviceId");

        // Notify victim that help is on the way
        if (victimDeviceId != null) {
            messagingTemplate.convertAndSend("/topic/alerts/" + victimDeviceId, Map.of(
                    "type", "RESPONDER_EN_ROUTE",
                    "emergencyId", emergencyId,
                    "responderName", responder.getName() != null ? responder.getName() : "Responder",
                    "responderRole", responder.getRole().name(),
                    "etaMinutes", eta,
                    "responderLat", responder.getLatitude() != null ? responder.getLatitude() : 0,
                    "responderLon", responder.getLongitude() != null ? responder.getLongitude() : 0
            ));
        }

        return ResponseEntity.ok(Map.of(
                "message", "Response accepted",
                "emergencyId", emergencyId,
                "responderId", responder.getId(),
                "etaMinutes", eta
        ));
    }

    private User getUser(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
