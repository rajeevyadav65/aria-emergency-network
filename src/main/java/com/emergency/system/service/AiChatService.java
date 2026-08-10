package com.emergency.system.service;

import com.emergency.system.dto.EmergencyDTOs.ChatRequest;
import com.emergency.system.dto.EmergencyDTOs.ChatResponse;
import com.emergency.system.model.ChatMessage;
import com.emergency.system.repository.ChatMessageRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.emergency.system.service.AriaAIEngine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * AI Chat service — powers the conversational chatbot and voice assistant.
 * Maintains per-session history. Detects if the user describes an emergency.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AiChatService {

    @Value("${anthropic.api.key:}")
    private String anthropicApiKey;

    @Value("${anthropic.api.model:claude-sonnet-4-20250514}")
    private String model;

    private final ChatMessageRepository chatRepo;
    private final ObjectMapper objectMapper;

    private static final OkHttpClient HTTP = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .build();

    private static final String SYSTEM_PROMPT = """
            You are ARIA — AI Response & Incident Assistant — embedded in an emergency response network app.
            Your role:
            1. Help users in distress with calm, clear, actionable guidance.
            2. Provide first-aid tips, safety instructions, and location advice.
            3. If you detect a real emergency (fall, chest pain, danger), respond with: start your message with "[EMERGENCY_DETECTED]"
            4. Keep responses concise and focused — the user may be stressed.
            5. If asked general questions unrelated to emergencies, answer helpfully but suggest they focus on safety.
            Always be empathetic, calm, and reassuring.
            """;

    public ChatResponse chat(ChatRequest request) {
        String sessionId = request.getSessionId();

        // Persist user message
        chatRepo.save(ChatMessage.builder()
                .sessionId(sessionId)
                .role(ChatMessage.MessageRole.USER)
                .content(request.getMessage())
                .build());

        // Build message history for context window (last 10 turns)
        List<ChatMessage> history = chatRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);
        List<Map<String, String>> messages = history.stream()
                .map(m -> Map.of(
                        "role", m.getRole() == ChatMessage.MessageRole.USER ? "user" : "assistant",
                        "content", m.getContent()))
                .collect(Collectors.toList());

        // Keep last 20 messages to respect token limits
        if (messages.size() > 20) {
            messages = messages.subList(messages.size() - 20, messages.size());
        }

        String reply;
        if (anthropicApiKey != null && !anthropicApiKey.isBlank()
                && !anthropicApiKey.equals("your-api-key-here")) {
            try {
                reply = callClaudeApi(messages);
            } catch (Exception e) {
                log.warn("Chat API call failed: {}", e.getMessage());
                reply = getFallbackReply(request.getMessage());
            }
        } else {
            reply = getFallbackReply(request.getMessage());
        }

        // Persist assistant reply
        chatRepo.save(ChatMessage.builder()
                .sessionId(sessionId)
                .role(ChatMessage.MessageRole.ASSISTANT)
                .content(reply)
                .build());

        boolean isEmergency = reply.startsWith("[EMERGENCY_DETECTED]");
        String cleanReply = reply.replace("[EMERGENCY_DETECTED]", "").trim();
        String suggestedAction = isEmergency ? "REPORT_EMERGENCY" : null;

        return ChatResponse.builder()
                .reply(cleanReply)
                .sessionId(sessionId)
                .timestamp(LocalDateTime.now())
                .isEmergency(isEmergency)
                .suggestedAction(suggestedAction)
                .build();
    }

    public List<ChatMessage> getHistory(String sessionId) {
        return chatRepo.findBySessionIdOrderByCreatedAtAsc(sessionId);
    }

    @org.springframework.transaction.annotation.Transactional
    public void clearHistory(String sessionId) {
        chatRepo.deleteBySessionId(sessionId);
    }

    private String callClaudeApi(List<Map<String, String>> messages) throws Exception {
        Map<String, Object> body = Map.of(
                "model", model,
                "max_tokens", 500,
                "system", SYSTEM_PROMPT,
                "messages", messages
        );

        Request req = new Request.Builder()
                .url("https://api.anthropic.com/v1/messages")
                .addHeader("x-api-key", anthropicApiKey)
                .addHeader("anthropic-version", "2023-06-01")
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(objectMapper.writeValueAsString(body),
                        MediaType.parse("application/json")))
                .build();

        try (Response response = HTTP.newCall(req).execute()) {
            if (!response.isSuccessful()) throw new RuntimeException("API error " + response.code());
            var parsed = objectMapper.readTree(response.body().string());
            return parsed.at("/content/0/text").asText();
        }
    }

    private String getFallbackReply(String message) {
        String lower = message.toLowerCase();
        if (lower.contains("fall") || lower.contains("fell") || lower.contains("injured"))
            return "[EMERGENCY_DETECTED] I can see you may have been injured. Stay still if possible — help is being notified. Are you able to move? Is anyone around you?";
        if (lower.contains("chest") || lower.contains("heart") || lower.contains("breathe"))
            return "[EMERGENCY_DETECTED] This sounds like a medical emergency. Call emergency services immediately (112 in India). Sit or lie down, stay calm, and unlock your door if possible.";
        if (lower.contains("help") || lower.contains("emergency") || lower.contains("danger"))
            return "[EMERGENCY_DETECTED] I'm alerting nearby users now. Stay on this chat. Can you describe your situation in more detail?";
        if (lower.contains("hello") || lower.contains("hi"))
            return "Hello! I'm ARIA, your emergency assistant. I'm here to help if you're in distress or need safety guidance. How can I assist you?";
        return "I'm here to help you stay safe. Please describe your situation and I'll provide guidance. If this is an emergency, tap the SOS button immediately.";
    }
}
