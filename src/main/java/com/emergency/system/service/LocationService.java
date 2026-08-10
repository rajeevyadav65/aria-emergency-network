package com.emergency.system.service;

import com.emergency.system.model.LocationHistory;
import com.emergency.system.model.User;
import com.emergency.system.repository.LocationHistoryRepository;
import com.emergency.system.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final UserRepository userRepository;
    private final LocationHistoryRepository locationHistoryRepository;

    @Value("${app.emergency.nearby-radius-meters:500}")
    private double nearbyRadiusMeters;

    private static final double EARTH_RADIUS_METERS = 6_371_000.0;

    public User updateUserLocation(String deviceId, double latitude, double longitude) {
        User user = userRepository.findByDeviceId(deviceId)
                .orElseGet(() -> User.builder()
                        .deviceId(deviceId).role(User.UserRole.GUEST).build());
        user.setLatitude(latitude);
        user.setLongitude(longitude);
        user.setLocationUpdatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);
        locationHistoryRepository.save(LocationHistory.builder()
                .deviceId(deviceId).latitude(latitude).longitude(longitude).build());
        return saved;
    }

    public List<LocationHistory> getLocationHistory(String deviceId) {
        LocalDateTime since = LocalDateTime.now().minusHours(24);
        return locationHistoryRepository
                .findByDeviceIdAndRecordedAtBetweenOrderByRecordedAtAsc(deviceId, since, LocalDateTime.now());
    }

    public List<User> findNearbyUsers(double latitude, double longitude) {
        List<User> nearby = userRepository.findUsersWithinRadius(latitude, longitude, nearbyRadiusMeters);
        log.info("Found {} users within {}m of ({}, {})", nearby.size(), nearbyRadiusMeters, latitude, longitude);
        return nearby;
    }

    public static double haversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.pow(Math.sin(dLon / 2), 2);
        return EARTH_RADIUS_METERS * 2 * Math.asin(Math.sqrt(a));
    }

    @Scheduled(fixedDelay = 3_600_000)
    public void pruneOldHistory() {
        int deleted = locationHistoryRepository.deleteOlderThan(LocalDateTime.now().minusHours(24));
        if (deleted > 0) log.info("Pruned {} old location history records", deleted);
    }
}
