package com.emergency.system.repository;

import com.emergency.system.model.Alert;
import com.emergency.system.model.Emergency;
import com.emergency.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByUser(User user);
    List<Alert> findByEmergency(Emergency emergency);
    boolean existsByEmergencyAndUser(Emergency emergency, User user);
}
