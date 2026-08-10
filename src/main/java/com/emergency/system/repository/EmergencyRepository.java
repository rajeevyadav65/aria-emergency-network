package com.emergency.system.repository;

import com.emergency.system.model.Emergency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmergencyRepository extends JpaRepository<Emergency, Long> {

    List<Emergency> findByStatus(Emergency.EmergencyStatus status);
    Page<Emergency> findByStatus(Emergency.EmergencyStatus status, Pageable pageable);
    List<Emergency> findByReportedByDeviceId(String deviceId);
    Page<Emergency> findByReportedByDeviceId(String deviceId, Pageable pageable);
    List<Emergency> findByRiskLevel(Emergency.RiskLevel riskLevel);

    long countByRiskLevel(Emergency.RiskLevel riskLevel);
    long countByStatus(Emergency.EmergencyStatus status);

    @Query("SELECT e FROM Emergency e ORDER BY e.createdAt DESC")
    Page<Emergency> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT e FROM Emergency e WHERE e.createdAt >= :since ORDER BY e.createdAt DESC")
    List<Emergency> findRecentEmergencies(@Param("since") LocalDateTime since);

    @Query("SELECT e FROM Emergency e WHERE e.status = 'ACTIVE' AND e.latitude IS NOT NULL")
    List<Emergency> findActiveWithLocation();
}
