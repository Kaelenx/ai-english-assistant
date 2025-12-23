package com.kaelenx.conversation.client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response DTO from AI orchestrator chat endpoint
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResponse {
    
    private String provider;
    private String model;
    private String replyText;
    private Integer tokenIn;
    private Integer tokenOut;
    private Long latencyMs;
}
