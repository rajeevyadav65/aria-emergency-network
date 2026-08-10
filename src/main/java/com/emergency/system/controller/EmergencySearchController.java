package com.emergency.system.controller;

import com.emergency.system.model.Emergency;
import com.emergency.system.service.EmergencySearchService;
import com.emergency.system.service.EmergencySearchService.SearchParams;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Search endpoint — supports keyword, risk level, status, device, time window, and geo radius.
 *
 * Example:
 *   GET /api/emergency/search?keyword=fall&riskLevel=HIGH&status=ACTIVE
 *   GET /api/emergency/search?lat=27.17&lon=78.00&radiusKm=1.0
 */
@RestController
@RequestMapping("/api/emergency")
@RequiredArgsConstructor
public class EmergencySearchController {

    private final EmergencySearchService searchService;

    @GetMapping("/search")
    public ResponseEntity<List<Emergency>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Emergency.RiskLevel riskLevel,
            @RequestParam(required = false) Emergency.EmergencyStatus status,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lon,
            @RequestParam(required = false) Double radiusKm) {

        SearchParams params = new SearchParams(
                keyword, riskLevel, status, deviceId, from, to, lat, lon, radiusKm);
        return ResponseEntity.ok(searchService.search(params));
    }
}
