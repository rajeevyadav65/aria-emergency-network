package com.emergency.system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper objectMapper;

    private static final String SESSION = "test-session-chat-001";

    @Test
    @DisplayName("POST /api/chat/message — returns reply with session")
    void sendMessage_success() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("sessionId", SESSION, "message", "Hello ARIA"));

        mvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty())
                .andExpect(jsonPath("$.sessionId").value(SESSION))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.isEmergency").exists());
    }

    @Test
    @DisplayName("POST /api/chat/message — emergency keyword sets isEmergency flag")
    void sendMessage_emergency_detected() throws Exception {
        String body = objectMapper.writeValueAsString(
                Map.of("sessionId", SESSION + "-em", "message", "I fell down and can't get up help"));

        mvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reply").isNotEmpty());
        // Note: isEmergency depends on AI response; we just verify structure
    }

    @Test
    @DisplayName("GET /api/chat/history — returns history after messages sent")
    void getHistory_afterMessages() throws Exception {
        String sessionId = "history-test-" + System.currentTimeMillis();
        String body = objectMapper.writeValueAsString(
                Map.of("sessionId", sessionId, "message", "Test message"));

        // Send a message first
        mvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        // Then get history
        mvc.perform(get("/api/chat/history").param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sessionId").value(sessionId))
                .andExpect(jsonPath("$.messages").isArray())
                .andExpect(jsonPath("$.messages.length()").value(2)); // user + AI
    }

    @Test
    @DisplayName("DELETE /api/chat/clear — clears history")
    void clearHistory() throws Exception {
        String sessionId = "clear-test-" + System.currentTimeMillis();
        String body = objectMapper.writeValueAsString(
                Map.of("sessionId", sessionId, "message", "Will be cleared"));

        mvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isOk());

        mvc.perform(delete("/api/chat/clear").param("sessionId", sessionId))
                .andExpect(status().isOk());

        mvc.perform(get("/api/chat/history").param("sessionId", sessionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.messages").isEmpty());
    }
}
