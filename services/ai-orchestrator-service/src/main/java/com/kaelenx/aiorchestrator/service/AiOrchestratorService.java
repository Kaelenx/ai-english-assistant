package com.kaelenx.aiorchestrator.service;

import com.kaelenx.aiorchestrator.dto.ChatRequest;
import com.kaelenx.aiorchestrator.dto.ChatResponse;
import com.kaelenx.aiorchestrator.entity.AiRequestLog;
import com.kaelenx.aiorchestrator.provider.LlmProvider;
import com.kaelenx.aiorchestrator.repository.AiRequestLogRepository;
import com.kaelenx.common.id.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Service for AI orchestration and chat completion
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiOrchestratorService {
    
    private final LlmProvider llmProvider;
    private final AiRequestLogRepository requestLogRepository;
    private final SnowflakeIdGenerator idGenerator;
    
    /**
     * Process a chat request and return AI response
     */
    public ChatResponse chat(ChatRequest request) {
        log.info("Processing chat request: conversationId={}, userId={}", 
                request.getConversationId(), request.getUserId());
        
        String status = "SUCCESS";
        String errorMessage = null;
        LlmProvider.LlmResponse llmResponse = null;
        
        try {
            // Call LLM provider
            llmResponse = llmProvider.chat(request);
            
            // Build response
            ChatResponse response = ChatResponse.builder()
                    .provider(llmProvider.getProviderName())
                    .model(llmProvider.getModelName())
                    .replyText(llmResponse.replyText())
                    .tokenIn(llmResponse.tokenIn())
                    .tokenOut(llmResponse.tokenOut())
                    .latencyMs(llmResponse.latencyMs())
                    .build();
            
            return response;
            
        } catch (Exception e) {
            log.error("Error processing chat request", e);
            status = "FAILED";
            errorMessage = e.getMessage();
            throw new RuntimeException("Failed to process chat request", e);
            
        } finally {
            // Always log the request for billing/usage tracking
            logRequest(request, status, llmResponse, errorMessage);
        }
    }
    
    private void logRequest(ChatRequest request, String status, 
                           LlmProvider.LlmResponse llmResponse, String errorMessage) {
        try {
            AiRequestLog log = AiRequestLog.builder()
                    .id(idGenerator.nextId())
                    .conversationId(request.getConversationId())
                    .userId(request.getUserId())
                    .sceneId(request.getSceneId())
                    .difficulty(request.getDifficulty())
                    .planTier(request.getPlanTier())
                    .provider(llmProvider.getProviderName())
                    .model(llmProvider.getModelName())
                    .status(status)
                    .tokenIn(llmResponse != null ? llmResponse.tokenIn() : null)
                    .tokenOut(llmResponse != null ? llmResponse.tokenOut() : null)
                    .latencyMs(llmResponse != null ? llmResponse.latencyMs() : null)
                    .errorMessage(errorMessage)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            requestLogRepository.insert(log);
            
        } catch (Exception e) {
            // Log but don't fail the request if logging fails
            this.log.error("Failed to log AI request", e);
        }
    }
}
