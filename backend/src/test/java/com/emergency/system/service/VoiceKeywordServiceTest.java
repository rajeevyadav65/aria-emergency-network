package com.emergency.system.service;

import com.emergency.system.model.User;
import com.emergency.system.model.VoiceKeyword;
import com.emergency.system.repository.VoiceKeywordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoiceKeywordServiceTest {

    @Mock private VoiceKeywordRepository keywordRepo;
    private PasswordEncoder passwordEncoder;
    private VoiceKeywordService service;
    private User user;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        service = new VoiceKeywordService(keywordRepo, passwordEncoder);
        user = User.builder().id(1L).email("alice@test.com").deviceId("dev-001").build();
    }

    @Test
    @DisplayName("setKeyword saves BCrypt hash, not plaintext")
    void setKeyword_savesBcryptHash() {
        when(keywordRepo.findByUser(user)).thenReturn(Optional.empty());
        when(keywordRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> result = service.setKeyword(user, "HELP123", "starts with H");

        assertThat(result.get("active")).isEqualTo(true);
        assertThat(result.get("hint")).isEqualTo("starts with H");

        // Verify the saved keyword is hashed
        verify(keywordRepo).save(argThat(vk -> {
            String hash = ((VoiceKeyword) vk).getKeywordHash();
            // BCrypt hash should start with $2a$ and not be the plaintext
            return hash.startsWith("$2") && !hash.equals("HELP123") && !hash.equals("help123");
        }));
    }

    @Test
    @DisplayName("setKeyword rejects keywords shorter than 4 chars")
    void setKeyword_tooShort_throws() {
        assertThatThrownBy(() -> service.setKeyword(user, "HI", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("at least 4 characters");
    }

    @Test
    @DisplayName("verifyKeyword returns true for correct keyword")
    void verifyKeyword_correct_returnsTrue() {
        String hash = passwordEncoder.encode("help123");
        VoiceKeyword vk = VoiceKeyword.builder()
                .user(user).keywordHash(hash).active(true).build();

        when(keywordRepo.findByUserAndActive(user, true)).thenReturn(Optional.of(vk));
        when(keywordRepo.save(any())).thenReturn(vk);

        assertThat(service.verifyKeyword(user, "HELP123")).isTrue();
    }

    @Test
    @DisplayName("verifyKeyword returns false for wrong keyword")
    void verifyKeyword_wrong_returnsFalse() {
        String hash = passwordEncoder.encode("help123");
        VoiceKeyword vk = VoiceKeyword.builder()
                .user(user).keywordHash(hash).active(true).build();

        when(keywordRepo.findByUserAndActive(user, true)).thenReturn(Optional.of(vk));

        assertThat(service.verifyKeyword(user, "WRONGWORD")).isFalse();
    }

    @Test
    @DisplayName("verifyKeyword returns false when no keyword set")
    void verifyKeyword_noKeyword_returnsFalse() {
        when(keywordRepo.findByUserAndActive(user, true)).thenReturn(Optional.empty());
        assertThat(service.verifyKeyword(user, "anything")).isFalse();
    }

    @Test
    @DisplayName("verifyKeyword is case-insensitive")
    void verifyKeyword_caseInsensitive() {
        String hash = passwordEncoder.encode("help123");
        VoiceKeyword vk = VoiceKeyword.builder()
                .user(user).keywordHash(hash).active(true).triggerCount(0).build();

        when(keywordRepo.findByUserAndActive(user, true)).thenReturn(Optional.of(vk));
        when(keywordRepo.save(any())).thenReturn(vk);

        // All variations should match
        assertThat(service.verifyKeyword(user, "HELP123")).isTrue();
        assertThat(service.verifyKeyword(user, "Help123")).isTrue();
        assertThat(service.verifyKeyword(user, "help123")).isTrue();
    }

    @Test
    @DisplayName("disableKeyword sets active=false")
    void disableKeyword_setsInactive() {
        VoiceKeyword vk = VoiceKeyword.builder()
                .user(user).keywordHash("hash").active(true).build();
        when(keywordRepo.findByUser(user)).thenReturn(Optional.of(vk));
        when(keywordRepo.save(any())).thenReturn(vk);

        service.disableKeyword(user);

        verify(keywordRepo).save(argThat(k -> !((VoiceKeyword) k).isActive()));
    }

    @Test
    @DisplayName("getStatus returns active=false when no keyword set")
    void getStatus_noKeyword_returnsInactive() {
        when(keywordRepo.findByUser(user)).thenReturn(Optional.empty());
        Map<String, Object> status = service.getStatus(user);
        assertThat(status.get("active")).isEqualTo(false);
    }
}
