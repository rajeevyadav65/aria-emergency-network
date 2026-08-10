package com.emergency.system.repository;

import com.emergency.system.model.LocationHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class LocationHistoryRepositoryTest {

    @Autowired
    private LocationHistoryRepository repo;

    @BeforeEach
    void setUp() {
        repo.deleteAll();
        LocalDateTime now = LocalDateTime.now();
        // 5 recent points for device A
        for (int i = 0; i < 5; i++) {
            repo.save(LocationHistory.builder()
                    .deviceId("dev-a")
                    .latitude(27.17 + i * 0.001)
                    .longitude(78.00 + i * 0.001)
                    .recordedAt(now.minusMinutes(i * 10))
                    .build());
        }
        // 2 old points for device A (>24h ago)
        for (int i = 0; i < 2; i++) {
            repo.save(LocationHistory.builder()
                    .deviceId("dev-a")
                    .latitude(27.10).longitude(78.00)
                    .recordedAt(now.minusHours(25 + i))
                    .build());
        }
        // 3 points for device B
        for (int i = 0; i < 3; i++) {
            repo.save(LocationHistory.builder()
                    .deviceId("dev-b")
                    .latitude(27.20).longitude(78.01)
                    .recordedAt(now.minusMinutes(i * 5))
                    .build());
        }
    }

    @Test
    @DisplayName("findByDeviceId returns all records for device, newest first")
    void findByDeviceId_orderedDesc() {
        List<LocationHistory> records =
                repo.findByDeviceIdOrderByRecordedAtDesc("dev-a");
        assertThat(records).hasSize(7);
        // Newest should come first
        assertThat(records.get(0).getRecordedAt())
                .isAfterOrEqualTo(records.get(1).getRecordedAt());
    }

    @Test
    @DisplayName("findByDeviceId with time window returns only recent records")
    void findByDeviceIdAndTimeBetween() {
        LocalDateTime from = LocalDateTime.now().minusHours(1);
        LocalDateTime to   = LocalDateTime.now();
        List<LocationHistory> recent =
                repo.findByDeviceIdAndRecordedAtBetweenOrderByRecordedAtAsc(
                        "dev-a", from, to);
        assertThat(recent).hasSize(5);
        assertThat(recent).allMatch(h -> !h.getRecordedAt().isBefore(from));
    }

    @Test
    @DisplayName("deleteOlderThan removes old records only")
    void deleteOlderThan() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        int deleted = repo.deleteOlderThan(cutoff);
        assertThat(deleted).isEqualTo(2); // only the 2 >24h records
        assertThat(repo.findByDeviceIdOrderByRecordedAtDesc("dev-a")).hasSize(5);
    }

    @Test
    @DisplayName("Device B history is independent of device A")
    void deviceIsolation() {
        assertThat(repo.findByDeviceIdOrderByRecordedAtDesc("dev-b")).hasSize(3);
        assertThat(repo.findByDeviceIdOrderByRecordedAtDesc("dev-c")).isEmpty();
    }
}
