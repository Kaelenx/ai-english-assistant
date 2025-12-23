package com.kaelenx.aiorchestrator.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request for AI chat completion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRequest {
    
    @NotNull(message = "conversationId is required")
    private Long conversationId;
    
    @NotNull(message = "userId is required")
    private Long userId;
    
    @NotNull(message = "sceneId is required")
    private Long sceneId;
    
    @NotBlank(message = "difficulty is required")
    private String difficulty;
    
    @NotBlank(message = "planTier is required")
    private String planTier;
    
    @NotBlank(message = "userText is required")
    private String userText;
    
    /**
     * Optional conversation history for context
     */
    private List<HistoryMessage> history;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryMessage {
        private String role;  // "user" or "assistant"
        private String content;
    }
}
