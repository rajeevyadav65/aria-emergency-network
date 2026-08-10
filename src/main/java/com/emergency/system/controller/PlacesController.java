package com.emergency.system.controller;

import com.emergency.system.service.PlacesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Custom Maps, Places & Geocoding API.
 * ZERO external API keys required — powered by OpenStreetMap.
 *
 * GET /api/maps/tiles              — OSM tile server config
 * GET /api/maps/reverse            — lat/lon → address
 * GET /api/maps/geocode            — text → coordinates
 * GET /api/maps/places/nearby      — find hospitals, pharmacies, police near you
 * GET /api/maps/route              — distance + ETA between two points
 */
@RestController
@RequestMapping("/api/maps")
@Tag(name = "Custom Maps API", description = "Free geocoding, places search and routing — no API key required")
@RequiredArgsConstructor
public class PlacesController {

    private final PlacesService placesService;

    /** OpenStreetMap tile server configuration for the frontend map */
    @Operation(summary = "Get map tile provider config (OpenStreetMap, free)")
    @GetMapping("/tiles")
    public ResponseEntity<Map<String, String>> tileConfig() {
        return ResponseEntity.ok(placesService.getTileConfig());
    }

    /** Convert coordinates to a human-readable address (reverse geocoding) */
    @Operation(summary = "Reverse geocode: coordinates → address")
    @GetMapping("/reverse")
    public ResponseEntity<Map<String, Object>> reverse(
            @RequestParam double lat, @RequestParam double lon) {
        return ResponseEntity.ok(placesService.reverseGeocode(lat, lon));
    }

    /** Search for a location by name (forward geocoding) */
    @Operation(summary = "Geocode: search text → coordinates list")
    @GetMapping("/geocode")
    public ResponseEntity<List<Map<String, Object>>> geocode(@RequestParam String q) {
        return ResponseEntity.ok(placesService.geocode(q));
    }

    /**
     * Find nearby emergency-relevant places.
     *
     * @param type hospital | pharmacy | police | fire_station | doctor | ambulance_station
     * @param radius search radius in metres (default 5000, max 20000)
     */
    @Operation(summary = "Find nearby places (hospitals, pharmacies, police stations)")
    @GetMapping("/places/nearby")
    public ResponseEntity<List<Map<String, Object>>> nearby(
            @RequestParam double lat,
            @RequestParam double lon,
            @RequestParam(defaultValue = "hospital") String type,
            @RequestParam(defaultValue = "5000") int radius) {
        int safeRadius = Math.min(radius, 20_000);
        return ResponseEntity.ok(placesService.findNearby(lat, lon, type, safeRadius));
    }

    /** Distance + ETA between two GPS points */
    @Operation(summary = "Route: straight-line distance + ETA estimates")
    @GetMapping("/route")
    public ResponseEntity<Map<String, Object>> route(
            @RequestParam double fromLat, @RequestParam double fromLon,
            @RequestParam double toLat,   @RequestParam double toLon) {
        return ResponseEntity.ok(placesService.calculateRoute(fromLat, fromLon, toLat, toLon));
    }
}
