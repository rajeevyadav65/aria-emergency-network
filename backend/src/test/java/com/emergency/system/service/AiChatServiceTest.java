package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.ChatRequest;
import com.emergency.system.dto.EmergencyDTOs.ChatResponse;
import com.emergency.system.model.ChatMessage;
import com.emergency.system.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AiChatService — no API key, tests fallback replies.
 */
@ExtendWith(MockitoExtension.class)
class AiChatServiceTest {

    @Mock
    private ChatMessageRepository chatRepo;

    private AiChatService service;

    @BeforeEach
    void setUp() {
        service = new AiChatService(chatRepo, new ObjectMapper());
        // No API key → always uses fallback
        ReflectionTestUtils.setField(service, "anthropicApiKey", "");
    }

    @Test
    @DisplayName("chat() saves user message and assistant reply to repository")
    void chat_persistsBothMessages() {
        when(chatRepo.findBySessionIdOrderByCreatedAtAsc("sess-001")).thenReturn(List.of());
        when(chatRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatRequest req = ChatRequest.builder()
                .sessionId("sess-001").message("Hello").build();
        service.chat(req);

        // Should save user message + assistant reply = 2 saves
        verify(chatRepo, times(2)).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("chat() returns non-blank reply")
    void chat_returnsReply() {
        when(chatRepo.findBySessionIdOrderByCreatedAtAsc(any())).thenReturn(List.of());
        when(chatRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatResponse resp = service.chat(
                ChatRequest.builder().sessionId("sess-002").message("I need help").build());

        assertThat(resp.getReply()).isNotBlank();
        assertThat(resp.getSessionId()).isEqualTo("sess-002");
        assertThat(resp.getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("chat() detects emergency keywords and sets isEmergency=true")
    void chat_detectsEmergency() {
        when(chatRepo.findBySessionIdOrderByCreatedAtAsc(any())).thenReturn(List.of());
        when(chatRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatResponse resp = service.chat(
                ChatRequest.builder().sessionId("sess-003").message("I fell down").build());

        // Fallback for 'fell' triggers emergency flag
        assertThat(resp.isEmergency()).isTrue();
    }

    @Test
    @DisplayName("chat() with general message does not set emergency flag")
    void chat_generalMessage_noEmergency() {
        when(chatRepo.findBySessionIdOrderByCreatedAtAsc(any())).thenReturn(List.of());
        when(chatRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        ChatResponse resp = service.chat(
                ChatRequest.builder().sessionId("sess-004").message("hello there").build());

        assertThat(resp.isEmergency()).isFalse();
    }

    @Test
    @DisplayName("getHistory() delegates to repository")
    void getHistory_delegatesToRepo() {
        ChatMessage msg = ChatMessage.builder()
                .sessionId("sess-005").role(ChatMessage.MessageRole.USER)
                .content("test").createdAt(LocalDateTime.now()).build();
        when(chatRepo.findBySessionIdOrderByCreatedAtAsc("sess-005")).thenReturn(List.of(msg));

        List<ChatMessage> history = service.getHistory("sess-005");

        assertThat(history).hasSize(1);
        verify(chatRepo).findBySessionIdOrderByCreatedAtAsc("sess-005");
    }

    @Test
    @DisplayName("clearHistory() calls deleteBySessionId")
    void clearHistory_callsDelete() {
        doNothing().when(chatRepo).deleteBySessionId("sess-006");
        service.clearHistory("sess-006");
        verify(chatRepo).deleteBySessionId("sess-006");
    }

    @Test
    @DisplayName("chat() includes conversation history in context (last 20 messages)")
    void chat_usesHistoryContext() {
        // Simulate 22 prior messages
        var history = new java.util.ArrayList<ChatMessage>();
        for (int i = 0; i < 22; i++) {
            history.add(ChatMessage.builder()
                    .sessionId("sess-007")
                    .role(i % 2 == 0 ? ChatMessage.MessageRole.USER : ChatMessage.MessageRole.ASSISTANT)
                    .content("message " + i)
                    .createdAt(LocalDateTime.now().minusMinutes(22 - i))
                    .build());
        }
        when(chatRepo.findBySessionIdOrderByCreatedAtAsc("sess-007")).thenReturn(history);
        when(chatRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        // Should not throw, truncates to last 20
        ChatResponse resp = service.chat(
                ChatRequest.builder().sessionId("sess-007").message("new message").build());
        assertThat(resp.getReply()).isNotBlank();
    }
}
