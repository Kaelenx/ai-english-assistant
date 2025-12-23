package com.kaelenx.aiorchestrator.provider;

import com.kaelenx.aiorchestrator.dto.ChatRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Mock implementation of Qwen LLM provider.
 * Returns configurable mock responses for quick testing.
 * 
 * TODO: Replace with actual Qwen SDK integration
 * Configuration needed:
 * - qwen.api.key: API key for Qwen service
 * - qwen.api.model: Model to use (e.g., "qwen-turbo")
 * - qwen.api.timeout-ms: Request timeout in milliseconds
 */
@Slf4j
@Component
public class QwenLlmProvider implements LlmProvider {
    
    private final String model;
    private final List<String> mockResponses;
    private final Random random = new Random();
    
    public QwenLlmProvider(
            @Value("${qwen.api.model:qwen-turbo}") String model,
            @Value("${qwen.api.mock-responses:Hello! How can I assist you today?|That's a great question!|Let me help you with that.}") 
            String mockResponsesStr) {
        this.model = model;
        this.mockResponses = Arrays.asList(mockResponsesStr.split("\\|"));
        log.info("QwenLlmProvider initialized with model={}, mockResponseCount={}", 
                model, mockResponses.size());
    }
    
    @Override
    public String getProviderName() {
        return "qwen-mock";
    }
    
    @Override
    public String getModelName() {
        return model;
    }
    
    @Override
    public LlmResponse chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        
        // TODO: Replace with actual Qwen SDK call
        // Example integration point:
        // 1. Build Qwen API request with request.getUserText() and request.getHistory()
        // 2. Call Qwen API: Generation gen = Generation.builder()...build();
        // 3. GenerationResult result = gen.call(...)
        // 4. Extract tokens from result.getUsage()
        
        // For now, return mock response
        String mockResponse = selectMockResponse(request);
        
        // Simulate processing time
        try {
            Thread.sleep(100 + random.nextInt(200));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        long latencyMs = System.currentTimeMillis() - startTime;
        
        log.info("Mock chat completion: conversationId={}, latency={}ms", 
                request.getConversationId(), latencyMs);
        
        return new LlmResponse(
                mockResponse,
                null,  // Token counts are null for mock
                null,
                latencyMs
        );
    }
    
    private String selectMockResponse(ChatRequest request) {
        // Simple mock logic - can be enhanced based on request context
        String userText = request.getUserText().toLowerCase();
        
        if (userText.contains("hello") || userText.contains("hi")) {
            return "Hello! I'm your AI English assistant. How can I help you practice English today?";
        } else if (userText.contains("help")) {
            return "Of course! I'm here to help you improve your English. What would you like to practice?";
        } else if (userText.contains("thank")) {
            return "You're welcome! Keep up the great work with your English learning!";
        }
        
        // Random response for other inputs
        return mockResponses.get(random.nextInt(mockResponses.size()));
    }
}
