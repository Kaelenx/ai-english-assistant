package com.kaelenx.conversation.client;

import feign.Feign;
import feign.Logger;
import feign.RequestLine;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Feign client for AI Orchestrator service
 */
public interface AiOrchestratorClient {
    
    @RequestLine("POST /internal/ai/chat")
    AiChatResponse chat(AiChatRequest request);
    
    /**
     * Configuration for creating the Feign client
     */
    @Configuration
    class AiOrchestratorClientConfig {
        
        @Bean
        public AiOrchestratorClient aiOrchestratorClient(
                @Value("${ai.orchestrator.base-url}") String baseUrl) {
            return Feign.builder()
                    .encoder(new JacksonEncoder())
                    .decoder(new JacksonDecoder())
                    .logger(new Slf4jLogger(AiOrchestratorClient.class))
                    .logLevel(Logger.Level.BASIC)
                    .target(AiOrchestratorClient.class, baseUrl);
        }
    }
}
