package com.emergency.system.repository;

import com.emergency.system.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByDeviceId(String deviceId);
    boolean existsByEmail(String email);
    boolean existsByDeviceId(String deviceId);
    List<User> findByRole(User.UserRole role);
    List<User> findByRoleAndIsOnDuty(User.UserRole role, Boolean onDuty);
    List<User> findByRoleAndIsAvailable(User.UserRole role, Boolean available);

    @Query("""
        SELECT u FROM User u
        WHERE u.latitude IS NOT NULL AND u.longitude IS NOT NULL
          AND (6371000 * 2 * ASIN(SQRT(
                POWER(SIN(RADIANS(:lat - u.latitude) / 2), 2) +
                COS(RADIANS(:lat)) * COS(RADIANS(u.latitude)) *
                POWER(SIN(RADIANS(:lon - u.longitude) / 2), 2)
              ))) <= :radiusMeters
    """)
    List<User> findUsersWithinRadius(@Param("lat") double latitude,
            @Param("lon") double longitude, @Param("radiusMeters") double radiusMeters);

    @Query("""
        SELECT u FROM User u
        WHERE u.latitude IS NOT NULL AND u.longitude IS NOT NULL
          AND u.role = :role
          AND (6371000 * 2 * ASIN(SQRT(
                POWER(SIN(RADIANS(:lat - u.latitude) / 2), 2) +
                COS(RADIANS(:lat)) * COS(RADIANS(u.latitude)) *
                POWER(SIN(RADIANS(:lon - u.longitude) / 2), 2)
              ))) <= :radiusMeters
        ORDER BY (6371000 * 2 * ASIN(SQRT(
                POWER(SIN(RADIANS(:lat - u.latitude) / 2), 2) +
                COS(RADIANS(:lat)) * COS(RADIANS(u.latitude)) *
                POWER(SIN(RADIANS(:lon - u.longitude) / 2), 2)
              ))) ASC
    """)
    List<User> findUsersWithinRadiusByRole(@Param("lat") double latitude,
            @Param("lon") double longitude, @Param("radiusMeters") double radiusMeters,
            @Param("role") User.UserRole role);
}
