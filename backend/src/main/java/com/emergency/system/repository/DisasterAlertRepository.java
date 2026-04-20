package com.emergency.system.repository;

import com.emergency.system.model.DisasterAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DisasterAlertRepository extends JpaRepository<DisasterAlert, Long> {

    List<DisasterAlert> findByAlertStatusOrderByIssuedAtDesc(
            DisasterAlert.AlertStatus status);

    List<DisasterAlert> findByTypeAndAlertStatus(
            DisasterAlert.DisasterType type, DisasterAlert.AlertStatus status);

    /** Find active alerts covering a geographic point (within radius) */
    @Query("""
        SELECT d FROM DisasterAlert d
        WHERE d.alertStatus = 'ACTIVE'
          AND d.epicenterLat IS NOT NULL
          AND (6371 * 2 * ASIN(SQRT(
                POWER(SIN(RADIANS(:lat - d.epicenterLat) / 2), 2) +
                COS(RADIANS(:lat)) * COS(RADIANS(d.epicenterLat)) *
                POWER(SIN(RADIANS(:lon - d.epicenterLon) / 2), 2)
              ))) <= d.radiusKm
    """)
    List<DisasterAlert> findActiveAlertsAffecting(
            @Param("lat") double latitude, @Param("lon") double longitude);

    boolean existsByExternalId(String externalId);

    List<DisasterAlert> findByIssuedAtAfterOrderByIssuedAtDesc(LocalDateTime since);
}
