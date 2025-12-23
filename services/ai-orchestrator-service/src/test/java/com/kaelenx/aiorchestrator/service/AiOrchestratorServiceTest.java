package com.kaelenx.aiorchestrator.service;

import com.kaelenx.aiorchestrator.dto.ChatRequest;
import com.kaelenx.aiorchestrator.dto.ChatResponse;
import com.kaelenx.aiorchestrator.entity.AiRequestLog;
import com.kaelenx.aiorchestrator.provider.LlmProvider;
import com.kaelenx.aiorchestrator.repository.AiRequestLogRepository;
import com.kaelenx.common.id.SnowflakeIdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AiOrchestratorServiceTest {
    
    @Mock
    private LlmProvider llmProvider;
    
    @Mock
    private AiRequestLogRepository requestLogRepository;
    
    @Mock
    private SnowflakeIdGenerator idGenerator;
    
    private AiOrchestratorService service;
    
    @BeforeEach
    void setUp() {
        service = new AiOrchestratorService(llmProvider, requestLogRepository, idGenerator);
    }
    
    @Test
    void testChat_Success() {
        // Arrange
        ChatRequest request = ChatRequest.builder()
                .conversationId(123L)
                .userId(1L)
                .sceneId(1L)
                .difficulty("EASY")
                .planTier("FREE")
                .userText("Hello")
                .build();
        
        LlmProvider.LlmResponse llmResponse = new LlmProvider.LlmResponse(
                "Hello! How can I help you?",
                10,
                20,
                150L
        );
        
        when(llmProvider.getProviderName()).thenReturn("qwen-mock");
        when(llmProvider.getModelName()).thenReturn("qwen-turbo");
        when(llmProvider.chat(request)).thenReturn(llmResponse);
        when(idGenerator.nextId()).thenReturn(1000L);
        
        // Act
        ChatResponse response = service.chat(request);
        
        // Assert
        assertNotNull(response);
        assertEquals("qwen-mock", response.getProvider());
        assertEquals("qwen-turbo", response.getModel());
        assertEquals("Hello! How can I help you?", response.getReplyText());
        assertEquals(10, response.getTokenIn());
        assertEquals(20, response.getTokenOut());
        assertEquals(150L, response.getLatencyMs());
        
        // Verify logging
        ArgumentCaptor<AiRequestLog> logCaptor = ArgumentCaptor.forClass(AiRequestLog.class);
        verify(requestLogRepository).insert(logCaptor.capture());
        
        AiRequestLog capturedLog = logCaptor.getValue();
        assertEquals(1000L, capturedLog.getId());
        assertEquals(123L, capturedLog.getConversationId());
        assertEquals("SUCCESS", capturedLog.getStatus());
        assertNull(capturedLog.getErrorMessage());
    }
    
    @Test
    void testChat_LogsRequestEvenOnFailure() {
        // Arrange
        ChatRequest request = ChatRequest.builder()
                .conversationId(123L)
                .userId(1L)
                .sceneId(1L)
                .difficulty("EASY")
                .planTier("FREE")
                .userText("Hello")
                .build();
        
        when(llmProvider.getProviderName()).thenReturn("qwen-mock");
        when(llmProvider.getModelName()).thenReturn("qwen-turbo");
        when(llmProvider.chat(request)).thenThrow(new RuntimeException("API Error"));
        when(idGenerator.nextId()).thenReturn(1000L);
        
        // Act & Assert
        assertThrows(RuntimeException.class, () -> service.chat(request));
        
        // Verify logging still happened
        ArgumentCaptor<AiRequestLog> logCaptor = ArgumentCaptor.forClass(AiRequestLog.class);
        verify(requestLogRepository).insert(logCaptor.capture());
        
        AiRequestLog capturedLog = logCaptor.getValue();
        assertEquals("FAILED", capturedLog.getStatus());
        assertEquals("API Error", capturedLog.getErrorMessage());
    }
}
