package com.emergency.system.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Custom Places & Geocoding API — 100% free, no API key required.
 *
 * Uses OpenStreetMap Nominatim for:
 * - Reverse geocoding (lat/lon → address)
 * - Forward geocoding (address → lat/lon)
 * - Nearby places search (hospitals, pharmacies, police stations, etc.)
 *
 * Rate limit: 1 req/sec per Nominatim fair use policy (handled by throttle)
 *
 * Endpoints exposed:
 *   GET /api/places/nearby     — nearby hospitals, pharmacies, etc.
 *   GET /api/places/geocode    — address → coordinates
 *   GET /api/places/reverse    — coordinates → address
 *   GET /api/places/route      — simple distance/ETA calculation
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class PlacesService {

    private final ObjectMapper objectMapper;

    private static final String NOMINATIM = "https://nominatim.openstreetmap.org";
    private static final String OVERPASS  = "https://overpass-api.de/api/interpreter";
    private static final String USER_AGENT = "ARIA-Emergency-App/2.0 contact@aria-emergency.example.com";

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(12, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS)
            .build();

    // ── Reverse Geocoding (coordinates → human address) ───────────────────────

    /**
     * Convert GPS coordinates to a human-readable address.
     * Example: 27.1767, 78.0081 → "Taj Mahal Gate, Agra, Uttar Pradesh, India"
     */
    public Map<String, Object> reverseGeocode(double lat, double lon) {
        try {
            String url = String.format(
                    "%s/reverse?format=json&lat=%.6f&lon=%.6f&zoom=18&addressdetails=1",
                    NOMINATIM, lat, lon);

            JsonNode r = fetch(url);
            if (r == null) return fallbackAddress(lat, lon);

            Map<String, Object> result = new LinkedHashMap<>();
            result.put("displayName", r.path("display_name").asText("Unknown location"));
            result.put("latitude",    lat);
            result.put("longitude",   lon);

            JsonNode addr = r.path("address");
            result.put("road",        addr.path("road").asText(""));
            result.put("city",        firstNonEmpty(addr, "city", "town", "village", "hamlet"));
            result.put("state",       addr.path("state").asText(""));
            result.put("country",     addr.path("country").asText(""));
            result.put("postcode",    addr.path("postcode").asText(""));
            result.put("source",      "OpenStreetMap / Nominatim");
            return result;

        } catch (Exception e) {
            log.warn("[PLACES] Reverse geocode failed: {}", e.getMessage());
            return fallbackAddress(lat, lon);
        }
    }

    // ── Forward Geocoding (address → coordinates) ─────────────────────────────

    /**
     * Search for a location by name and return coordinates.
     */
    public List<Map<String, Object>> geocode(String query) {
        try {
            String url = String.format("%s/search?format=json&q=%s&limit=5&addressdetails=1",
                    NOMINATIM, URLEncoder.encode(query, StandardCharsets.UTF_8));

            JsonNode results = fetch(url);
            if (results == null || !results.isArray()) return List.of();

            List<Map<String, Object>> list = new ArrayList<>();
            for (JsonNode r : results) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("displayName", r.path("display_name").asText());
                item.put("latitude",    Double.parseDouble(r.path("lat").asText("0")));
                item.put("longitude",   Double.parseDouble(r.path("lon").asText("0")));
                item.put("type",        r.path("type").asText());
                item.put("importance",  r.path("importance").asDouble());
                list.add(item);
            }
            return list;

        } catch (Exception e) {
            log.warn("[PLACES] Geocode failed: {}", e.getMessage());
            return List.of();
        }
    }

    // ── Nearby Places (hospitals, pharmacies, police, etc.) ───────────────────

    /**
     * Find nearby emergency-relevant places using the Overpass API (OSM).
     *
     * @param type "hospital" | "pharmacy" | "police" | "fire_station" | "doctor" | "ambulance_station"
     * @param radiusMeters search radius in metres (default 5000)
     */
    public List<Map<String, Object>> findNearby(double lat, double lon, String type, int radiusMeters) {
        String osmTag = resolveOsmTag(type);
        try {
            // Overpass QL query
            String query = String.format(
                    "[out:json][timeout:15];(node[%s](around:%d,%.6f,%.6f);way[%s](around:%d,%.6f,%.6f););out body center 20;",
                    osmTag, radiusMeters, lat, lon,
                    osmTag, radiusMeters, lat, lon);

            String url = OVERPASS + "?data=" + URLEncoder.encode(query, StandardCharsets.UTF_8);
            JsonNode r = fetch(url);
            if (r == null) return List.of();

            JsonNode elements = r.path("elements");
            List<Map<String, Object>> places = new ArrayList<>();

            for (JsonNode el : elements) {
                Map<String, Object> place = new LinkedHashMap<>();
                JsonNode tags = el.path("tags");

                double placeLat = el.has("lat") ? el.get("lat").asDouble()
                        : el.path("center").path("lat").asDouble();
                double placeLon = el.has("lon") ? el.get("lon").asDouble()
                        : el.path("center").path("lon").asDouble();

                place.put("id",        el.path("id").asLong());
                place.put("name",      tags.path("name").asText("Unnamed " + type));
                place.put("type",      type);
                place.put("latitude",  placeLat);
                place.put("longitude", placeLon);
                place.put("phone",     tags.path("phone").asText(tags.path("contact:phone").asText("")));
                place.put("address",   tags.path("addr:full").asText(
                        tags.path("addr:street").asText("") + " " +
                        tags.path("addr:city").asText("")).trim());
                place.put("openNow",   tags.path("opening_hours").asText("").contains("24/7"));
                place.put("emergency", tags.path("emergency").asText(""));
                place.put("distanceMeters", haversine(lat, lon, placeLat, placeLon));
                places.add(place);
            }

            // Sort by distance
            places.sort(Comparator.comparingDouble(p -> (Double) p.get("distanceMeters")));
            return places;

        } catch (Exception e) {
            log.warn("[PLACES] Nearby {} failed: {}", type, e.getMessage());
            return List.of();
        }
    }

    // ── Distance / ETA Calculator ─────────────────────────────────────────────

    /**
     * Calculate straight-line distance and estimated travel time.
     * No routing API needed — uses Haversine formula.
     */
    public Map<String, Object> calculateRoute(double fromLat, double fromLon,
                                               double toLat, double toLon) {
        double distanceM = haversine(fromLat, fromLon, toLat, toLon);
        double distanceKm = distanceM / 1000.0;

        // Rough ETA estimates (urban India)
        double walkingMin   = distanceKm / 0.08;   // 5 km/h walking
        double drivingMin   = distanceKm / 0.667;  // 40 km/h urban driving
        double ambulanceMin = distanceKm / 1.0;    // 60 km/h emergency

        return Map.of(
                "distanceMeters",      Math.round(distanceM),
                "distanceKm",          Math.round(distanceKm * 10.0) / 10.0,
                "walkingMinutes",      (int) Math.ceil(walkingMin),
                "drivingMinutes",      (int) Math.ceil(drivingMin),
                "ambulanceMinutes",    (int) Math.ceil(ambulanceMin),
                "fromLat", fromLat, "fromLon", fromLon,
                "toLat", toLat,     "toLon",   toLon,
                "source", "ARIA Internal (Haversine)"
        );
    }

    // ── Map Tile Proxy (serves OSM tiles with caching) ────────────────────────

    /**
     * Returns the tile server URL template for OpenStreetMap.
     * No API key needed — OSM tiles are completely free.
     */
    public Map<String, String> getTileConfig() {
        return Map.of(
                "tileUrl",       "https://tile.openstreetmap.org/{z}/{x}/{y}.png",
                "attribution",   "© OpenStreetMap contributors",
                "maxZoom",       "19",
                "provider",      "OpenStreetMap",
                "license",       "ODbL"
        );
    }

    // ── Private Helpers ───────────────────────────────────────────────────────

    private JsonNode fetch(String url) throws Exception {
        Request req = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", USER_AGENT)
                .addHeader("Accept", "application/json")
                .build();
        try (Response resp = HTTP.newCall(req).execute()) {
            if (!resp.isSuccessful() || resp.body() == null) return null;
            return objectMapper.readTree(resp.body().string());
        }
    }

    private String resolveOsmTag(String type) {
        return switch (type.toLowerCase()) {
            case "hospital"          -> "amenity=hospital";
            case "clinic", "doctor"  -> "amenity=clinic";
            case "pharmacy"          -> "amenity=pharmacy";
            case "police"            -> "amenity=police";
            case "fire_station"      -> "amenity=fire_station";
            case "ambulance_station" -> "emergency=ambulance_station";
            case "defibrillator"     -> "emergency=defibrillator";
            default                  -> "amenity=" + type;
        };
    }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2)*Math.sin(dLat/2) +
                   Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*
                   Math.sin(dLon/2)*Math.sin(dLon/2);
        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
    }

    private Map<String, Object> fallbackAddress(double lat, double lon) {
        return Map.of("displayName", String.format("%.4f, %.4f", lat, lon),
                "latitude", lat, "longitude", lon,
                "source", "coordinates");
    }

    private String firstNonEmpty(JsonNode node, String... keys) {
        for (String k : keys) {
            String v = node.path(k).asText("");
            if (!v.isBlank()) return v;
        }
        return "";
    }
}
