package com.kaelenx.aiorchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response from AI chat completion
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * AI provider name (e.g., "qwen", "openai")
     */
    private String provider;
    
    /**
     * Model name used (e.g., "qwen-turbo")
     */
    private String model;
    
    /**
     * AI generated reply text
     */
    private String replyText;
    
    /**
     * Input tokens consumed (may be null for mock)
     */
    private Integer tokenIn;
    
    /**
     * Output tokens consumed (may be null for mock)
     */
    private Integer tokenOut;
    
    /**
     * Latency in milliseconds
     */
    private Long latencyMs;
}
