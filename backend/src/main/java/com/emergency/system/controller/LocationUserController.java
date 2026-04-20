package com.emergency.system.controller;

import com.emergency.system.dto.EmergencyDTOs.LocationUpdateRequest;
import com.emergency.system.model.User;
import com.emergency.system.repository.UserRepository;
import com.emergency.system.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-location") // FIX: Base path add kiya taaki conflict na ho
public class LocationUserController {

    private final LocationService locationService;
    private final UserRepository userRepository;

    // Ab iska path hoga: POST /api/user-location/update
    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateLocation(
            @RequestBody LocationUpdateRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        String deviceId = resolveDeviceId(request.getDeviceId(), principal);
        User user = locationService.updateUserLocation(
                deviceId, request.getLatitude(), request.getLongitude());

        return ResponseEntity.ok(Map.of(
                "status", "updated",
                "deviceId", user.getDeviceId(),
                "latitude", user.getLatitude(),
                "longitude", user.getLongitude()
        ));
    }

    // Ab iska path hoga: GET /api/user-location/me
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails principal) {
        if (principal == null)
            return ResponseEntity.status(401).body(Map.of("error", "Not authenticated"));

        return userRepository.findByEmail(principal.getUsername())
                .map(user -> ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "name", user.getName() != null ? user.getName() : "",
                        "email", user.getEmail() != null ? user.getEmail() : "",
                        "deviceId", user.getDeviceId(),
                        "role", user.getRole().name(),
                        "latitude", user.getLatitude() != null ? user.getLatitude() : 0.0,
                        "longitude", user.getLongitude() != null ? user.getLongitude() : 0.0
                )))
                .orElse(ResponseEntity.notFound().build());
    }

    private String resolveDeviceId(String reqId, UserDetails principal) {
        if (reqId != null && !reqId.isBlank()) return reqId;
        if (principal != null) {
            return userRepository.findByEmail(principal.getUsername())
                    .map(User::getDeviceId)
                    .orElse(UUID.randomUUID().toString());
        }
        return UUID.randomUUID().toString();
    }
}