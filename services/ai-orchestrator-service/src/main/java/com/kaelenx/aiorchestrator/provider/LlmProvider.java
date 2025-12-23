package com.kaelenx.aiorchestrator.provider;

import com.kaelenx.aiorchestrator.dto.ChatRequest;

/**
 * Interface for LLM providers.
 * Implementations should handle the actual interaction with AI services.
 */
public interface LlmProvider {
    
    /**
     * Get the provider name (e.g., "qwen", "openai")
     */
    String getProviderName();
    
    /**
     * Get the model name being used
     */
    String getModelName();
    
    /**
     * Generate a chat completion response
     * 
     * @param request Chat request with user message and context
     * @return LlmResponse with generated text and token usage
     */
    LlmResponse chat(ChatRequest request);
    
    /**
     * Response from LLM provider
     */
    record LlmResponse(
        String replyText,
        Integer tokenIn,
        Integer tokenOut,
        Long latencyMs
    ) {}
}
