package com.kaelenx.conversation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kaelenx.conversation.client.AiChatRequest;
import com.kaelenx.conversation.client.AiChatResponse;
import com.kaelenx.conversation.client.AiOrchestratorClient;
import com.kaelenx.conversation.dto.*;
import com.kaelenx.conversation.entity.Conversation;
import com.kaelenx.conversation.entity.Message;
import com.kaelenx.conversation.repository.ConversationRepository;
import com.kaelenx.conversation.repository.MessageRepository;
import com.kaelenx.common.id.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for conversation management
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConversationService {
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final AiOrchestratorClient aiOrchestratorClient;
    private final SnowflakeIdGenerator idGenerator;
    private final ObjectMapper objectMapper;
    
    /**
     * Create a new conversation
     */
    @Transactional
    public CreateConversationResponse createConversation(CreateConversationRequest request, Long userId) {
        log.info("Creating conversation: userId={}, sceneId={}, difficulty={}", 
                userId, request.getSceneId(), request.getDifficulty());
        
        Conversation conversation = Conversation.builder()
                .id(idGenerator.nextId())
                .userId(userId)
                .sceneId(request.getSceneId())
                .difficulty(request.getDifficulty())
                .status("ACTIVE")
                .planTier(request.getPlanTier())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        conversationRepository.insert(conversation);
        
        log.info("Conversation created: conversationId={}", conversation.getId());
        
        return CreateConversationResponse.builder()
                .conversationId(conversation.getId())
                .status(conversation.getStatus())
                .build();
    }
    
    /**
     * Send a text message and get AI response
     */
    @Transactional
    public SendTextMessageResponse sendTextMessage(Long conversationId, SendTextMessageRequest request, Long userId) {
        log.info("Sending text message: conversationId={}, userId={}", conversationId, userId);
        
        // Verify conversation exists and belongs to user
        Conversation conversation = conversationRepository.selectById(conversationId);
        if (conversation == null) {
            throw new RuntimeException("Conversation not found: " + conversationId);
        }
        if (!conversation.getUserId().equals(userId)) {
            throw new RuntimeException("Conversation does not belong to user");
        }
        
        // Create user message with FINAL status
        Message userMessage = Message.builder()
                .id(idGenerator.nextId())
                .conversationId(conversationId)
                .senderRole("USER")
                .contentType("TEXT")
                .textContent(request.getText())
                .status("FINAL")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        messageRepository.insert(userMessage);
        log.info("User message created: messageId={}", userMessage.getId());
        
        // Call AI orchestrator service
        AiChatRequest aiRequest = AiChatRequest.builder()
                .conversationId(conversationId)
                .userId(userId)
                .sceneId(conversation.getSceneId())
                .difficulty(conversation.getDifficulty())
                .planTier(conversation.getPlanTier())
                .userText(request.getText())
                .build();
        
        AiChatResponse aiResponse;
        try {
            aiResponse = aiOrchestratorClient.chat(aiRequest);
            log.info("AI response received: provider={}, model={}, latency={}ms", 
                    aiResponse.getProvider(), aiResponse.getModel(), aiResponse.getLatencyMs());
        } catch (Exception e) {
            log.error("Failed to get AI response", e);
            throw new RuntimeException("Failed to get AI response: " + e.getMessage(), e);
        }
        
        // Create assistant message with FINAL status
        String providerTrace = buildProviderTrace(aiResponse);
        
        Message assistantMessage = Message.builder()
                .id(idGenerator.nextId())
                .conversationId(conversationId)
                .senderRole("ASSISTANT")
                .contentType("TEXT")
                .textContent(aiResponse.getReplyText())
                .status("FINAL")
                .providerTrace(providerTrace)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        messageRepository.insert(assistantMessage);
        log.info("Assistant message created: messageId={}", assistantMessage.getId());
        
        // Update conversation updated_at
        conversation.setUpdatedAt(LocalDateTime.now());
        conversationRepository.updateById(conversation);
        
        return SendTextMessageResponse.builder()
                .userMessageId(userMessage.getId())
                .assistantMessageId(assistantMessage.getId())
                .replyText(aiResponse.getReplyText())
                .build();
    }
    
    private String buildProviderTrace(AiChatResponse aiResponse) {
        try {
            Map<String, Object> trace = new HashMap<>();
            trace.put("provider", aiResponse.getProvider());
            trace.put("model", aiResponse.getModel());
            trace.put("tokenIn", aiResponse.getTokenIn());
            trace.put("tokenOut", aiResponse.getTokenOut());
            trace.put("latencyMs", aiResponse.getLatencyMs());
            return objectMapper.writeValueAsString(trace);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize provider trace", e);
            return "{}";
        }
    }
}
