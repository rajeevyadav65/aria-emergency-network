package com.emergency.system.controller;

import com.emergency.system.dto.EmergencyDTOs.*;
import com.emergency.system.model.ChatMessage;
import com.emergency.system.service.AiChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * AI Chatbot endpoints.
 * POST /api/chat/message   — send a message, get AI reply
 * GET  /api/chat/history   — get chat history for a session
 * DELETE /api/chat/clear   — clear chat history
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final AiChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        return ResponseEntity.ok(chatService.chat(request));
    }

    @GetMapping("/history")
    public ResponseEntity<ChatHistoryResponse> getHistory(
            @RequestParam String sessionId) {
        List<ChatMessage> messages = chatService.getHistory(sessionId);
        List<ChatMessageDto> dtos = messages.stream()
                .map(m -> ChatMessageDto.builder()
                        .role(m.getRole().name().toLowerCase())
                        .content(m.getContent())
                        .createdAt(m.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(ChatHistoryResponse.builder()
                .sessionId(sessionId)
                .messages(dtos)
                .build());
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Void> clearHistory(@RequestParam String sessionId) {
        chatService.clearHistory(sessionId);
        return ResponseEntity.ok().build();
    }
}
