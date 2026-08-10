package com.emergency.system.controller;

import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.service.EmergencyService;
import com.emergency.system.service.VoiceKeywordService;
import com.emergency.system.dto.EmergencyDTOs.EmergencyRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Voice keyword (secret trigger) endpoints.
 *
 * POST /api/voice/keyword          — set secret keyword
 * GET  /api/voice/keyword/status   — check if keyword is set
 * POST /api/voice/keyword/disable  — disable without deleting
 * POST /api/voice/trigger          — device reports keyword was spoken (auto-emergency)
 */
@RestController
@RequestMapping("/api/voice")
@Tag(name = "Voice Trigger", description = "Secret voice keyword emergency trigger")
@RequiredArgsConstructor
public class VoiceKeywordController {

    private final VoiceKeywordService voiceService;
    private final EmergencyService emergencyService;
    private final UserRepository userRepository;

    @Operation(summary = "Set or update the secret voice trigger keyword")
    @PostMapping("/keyword")
    public ResponseEntity<Map<String, Object>> setKeyword(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, String> body) {
        User user = getUser(principal);
        return ResponseEntity.ok(
                voiceService.setKeyword(user, body.get("keyword"), body.get("hint")));
    }

    @Operation(summary = "Get voice keyword status (active/inactive, hint, trigger count)")
    @GetMapping("/keyword/status")
    public ResponseEntity<Map<String, Object>> getStatus(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(voiceService.getStatus(getUser(principal)));
    }

    @Operation(summary = "Disable the voice trigger keyword")
    @PostMapping("/keyword/disable")
    public ResponseEntity<Map<String, String>> disable(
            @AuthenticationPrincipal UserDetails principal) {
        voiceService.disableKeyword(getUser(principal));
        return ResponseEntity.ok(Map.of("message", "Voice keyword disabled"));
    }

    /**
     * Called silently by the device when the secret keyword is detected.
     * Triggers an emergency with HIGH risk level automatically.
     *
     * Body: { latitude, longitude, deviceId, spokenText (optional, for server verify) }
     */
    @Operation(summary = "Silent emergency trigger — called when keyword is detected")
    @PostMapping("/trigger")
    public ResponseEntity<Map<String, Object>> trigger(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody Map<String, Object> body) {

        Double lat  = body.get("latitude")  != null ? ((Number) body.get("latitude")).doubleValue()  : 0.0;
        Double lon  = body.get("longitude") != null ? ((Number) body.get("longitude")).doubleValue() : 0.0;
        String did  = (String) body.get("deviceId");

        // Auto-create HIGH risk emergency silently
        EmergencyRequest req = EmergencyRequest.builder()
                .message("SILENT SOS — Voice keyword triggered")
                .latitude(lat).longitude(lon)
                .fallDetected(false)
                .movement("UNKNOWN")
                .userResponse("NEED_HELP")
                .deviceId(did)
                .build();

        var response = emergencyService.processEmergency(req);
        return ResponseEntity.ok(Map.of(
                "triggered", true,
                "emergencyId", response.getEmergencyId(),
                "riskLevel", response.getRiskLevel(),
                "nearbyAlerted", response.getNearbyUsersAlerted()
        ));
    }

    private User getUser(UserDetails principal) {
        return userRepository.findByEmail(principal.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
