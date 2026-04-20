package com.emergency.system.repository;

import com.emergency.system.model.User;
import com.emergency.system.model.VoiceKeyword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoiceKeywordRepository extends JpaRepository<VoiceKeyword, Long> {
    Optional<VoiceKeyword> findByUserAndActive(User user, boolean active);
    Optional<VoiceKeyword> findByUser(User user);
    boolean existsByUser(User user);
}
