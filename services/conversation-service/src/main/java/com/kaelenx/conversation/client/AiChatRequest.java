package com.kaelenx.conversation.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for AI orchestrator chat endpoint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatRequest {
    
    private Long conversationId;
    private Long userId;
    private Long sceneId;
    private String difficulty;
    private String planTier;
    private String userText;
    private List<HistoryMessage> history;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryMessage {
        private String role;
        private String content;
    }
}
