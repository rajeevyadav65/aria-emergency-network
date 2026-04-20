package com.emergency.system.repository;

import com.emergency.system.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JPA slice tests for UserRepository — only loads JPA layer (no web, no security).
 */
@DataJpaTest
class UserRepositoryTest {

    @Autowired private UserRepository userRepository;

    // Agra, India coordinates
    private static final double BASE_LAT = 27.1767;
    private static final double BASE_LON = 78.0081;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        // 100m away
        save("dev-near", 27.1776, 78.0090);
        // 400m away
        save("dev-medium", 27.1803, 78.0120);
        // 2km away (far)
        save("dev-far", 27.1940, 78.0250);
        // No location
        save("dev-noloc", null, null);
    }

    private void save(String deviceId, Double lat, Double lon) {
        userRepository.save(User.builder()
                .deviceId(deviceId)
                .role(User.UserRole.GUEST)
                .latitude(lat).longitude(lon)
                .locationUpdatedAt(lat != null ? LocalDateTime.now() : null)
                .build());
    }

    @Test
    @DisplayName("findUsersWithinRadius 500m — returns near and medium but not far")
    void findWithin500m() {
        List<User> users = userRepository.findUsersWithinRadius(BASE_LAT, BASE_LON, 500);
        List<String> ids = users.stream().map(User::getDeviceId).toList();

        assertThat(ids).contains("dev-near", "dev-medium");
        assertThat(ids).doesNotContain("dev-far", "dev-noloc");
    }

    @Test
    @DisplayName("findUsersWithinRadius 150m — returns only nearest")
    void findWithin150m() {
        List<User> users = userRepository.findUsersWithinRadius(BASE_LAT, BASE_LON, 150);
        assertThat(users).hasSize(1);
        assertThat(users.get(0).getDeviceId()).isEqualTo("dev-near");
    }

    @Test
    @DisplayName("findUsersWithinRadius 5000m — returns all with location")
    void findWithin5km() {
        List<User> users = userRepository.findUsersWithinRadius(BASE_LAT, BASE_LON, 5000);
        assertThat(users).hasSize(3);
    }

    @Test
    @DisplayName("findByDeviceId — returns correct user")
    void findByDeviceId() {
        assertThat(userRepository.findByDeviceId("dev-near")).isPresent();
        assertThat(userRepository.findByDeviceId("non-existent")).isEmpty();
    }

    @Test
    @DisplayName("existsByDeviceId — correct results")
    void existsByDeviceId() {
        assertThat(userRepository.existsByDeviceId("dev-far")).isTrue();
        assertThat(userRepository.existsByDeviceId("ghost")).isFalse();
    }

    @Test
    @DisplayName("findByRole returns users with matching role")
    void findByRole_returnsMatchingUsers() {
        // Seeded by DataSeeder: dr.sharma@aria.com is DOCTOR
        var doctors = userRepository.findByRole(User.UserRole.DOCTOR);
        assertThat(doctors).isNotEmpty();
        doctors.forEach(u -> assertThat(u.getRole()).isEqualTo(User.UserRole.DOCTOR));
    }

    @Test
    @DisplayName("findByRole AMBULANCE returns ambulance users")
    void findByRole_ambulance() {
        var ambs = userRepository.findByRole(User.UserRole.AMBULANCE);
        assertThat(ambs).isNotEmpty();
        ambs.forEach(u -> assertThat(u.getRole()).isEqualTo(User.UserRole.AMBULANCE));
    }

    @Test
    @DisplayName("findByRoleAndIsOnDuty returns only on-duty users of that role")
    void findByRoleAndIsOnDuty_filtersCorrectly() {
        var onDuty = userRepository.findByRoleAndIsOnDuty(User.UserRole.DOCTOR, true);
        onDuty.forEach(u -> {
            assertThat(u.getRole()).isEqualTo(User.UserRole.DOCTOR);
            assertThat(u.getIsOnDuty()).isTrue();
        });
    }

    @Test
    @DisplayName("findUsersWithinRadiusByRole returns doctor within radius")
    void findUsersWithinRadiusByRole_doctorInRange() {
        // dr.sharma@aria.com is at 27.1850, 78.0200 — within 5km of 27.1767, 78.0081
        var nearby = userRepository.findUsersWithinRadiusByRole(
                27.1767, 78.0081, 5000, User.UserRole.DOCTOR);
        assertThat(nearby).isNotEmpty();
        nearby.forEach(u -> assertThat(u.getRole()).isEqualTo(User.UserRole.DOCTOR));
    }

    @Test
    @DisplayName("findUsersWithinRadiusByRole returns empty for tiny radius")
    void findUsersWithinRadiusByRole_tinyRadius() {
        var nearby = userRepository.findUsersWithinRadiusByRole(
                0.0, 0.0, 1, User.UserRole.DOCTOR); // middle of Atlantic
        assertThat(nearby).isEmpty();
    }

}