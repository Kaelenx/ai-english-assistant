package com.kaelenx.conversation.service;

import com.kaelenx.conversation.client.AiChatRequest;
import com.kaelenx.conversation.client.AiChatResponse;
import com.kaelenx.conversation.client.AiOrchestratorClient;
import com.kaelenx.conversation.dto.*;
import com.kaelenx.conversation.entity.Conversation;
import com.kaelenx.conversation.entity.Message;
import com.kaelenx.conversation.repository.ConversationRepository;
import com.kaelenx.conversation.repository.MessageRepository;
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
class ConversationServiceTest {
    
    @Mock
    private ConversationRepository conversationRepository;
    
    @Mock
    private MessageRepository messageRepository;
    
    @Mock
    private AiOrchestratorClient aiOrchestratorClient;
    
    @Mock
    private SnowflakeIdGenerator idGenerator;
    
    private ConversationService service;
    
    @BeforeEach
    void setUp() {
        service = new ConversationService(conversationRepository, messageRepository, 
                aiOrchestratorClient, idGenerator);
    }
    
    @Test
    void testCreateConversation() {
        // Arrange
        CreateConversationRequest request = CreateConversationRequest.builder()
                .sceneId(1L)
                .difficulty("EASY")
                .planTier("FREE")
                .build();
        
        when(idGenerator.nextId()).thenReturn(1000L);
        
        // Act
        CreateConversationResponse response = service.createConversation(request, 1L);
        
        // Assert
        assertNotNull(response);
        assertEquals(1000L, response.getConversationId());
        assertEquals("ACTIVE", response.getStatus());
        
        ArgumentCaptor<Conversation> captor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).insert(captor.capture());
        
        Conversation captured = captor.getValue();
        assertEquals(1000L, captured.getId());
        assertEquals(1L, captured.getUserId());
        assertEquals(1L, captured.getSceneId());
        assertEquals("EASY", captured.getDifficulty());
        assertEquals("ACTIVE", captured.getStatus());
        assertEquals("FREE", captured.getPlanTier());
    }
    
    @Test
    void testSendTextMessage() {
        // Arrange
        Long conversationId = 1000L;
        Long userId = 1L;
        
        Conversation conversation = Conversation.builder()
                .id(conversationId)
                .userId(userId)
                .sceneId(1L)
                .difficulty("EASY")
                .status("ACTIVE")
                .planTier("FREE")
                .build();
        
        SendTextMessageRequest request = SendTextMessageRequest.builder()
                .text("Hello")
                .build();
        
        AiChatResponse aiResponse = AiChatResponse.builder()
                .provider("qwen-mock")
                .model("qwen-turbo")
                .replyText("Hello! How can I help you?")
                .tokenIn(10)
                .tokenOut(20)
                .latencyMs(150L)
                .build();
        
        when(conversationRepository.selectById(conversationId)).thenReturn(conversation);
        when(idGenerator.nextId()).thenReturn(2000L, 3000L);
        when(aiOrchestratorClient.chat(any(AiChatRequest.class))).thenReturn(aiResponse);
        
        // Act
        SendTextMessageResponse response = service.sendTextMessage(conversationId, request, userId);
        
        // Assert
        assertNotNull(response);
        assertEquals(2000L, response.getUserMessageId());
        assertEquals(3000L, response.getAssistantMessageId());
        assertEquals("Hello! How can I help you?", response.getReplyText());
        
        // Verify user message was created
        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(messageRepository, times(2)).insert(messageCaptor.capture());
        
        Message userMessage = messageCaptor.getAllValues().get(0);
        assertEquals(2000L, userMessage.getId());
        assertEquals("USER", userMessage.getSenderRole());
        assertEquals("Hello", userMessage.getTextContent());
        assertEquals("FINAL", userMessage.getStatus());
        
        Message assistantMessage = messageCaptor.getAllValues().get(1);
        assertEquals(3000L, assistantMessage.getId());
        assertEquals("ASSISTANT", assistantMessage.getSenderRole());
        assertEquals("Hello! How can I help you?", assistantMessage.getTextContent());
        assertEquals("FINAL", assistantMessage.getStatus());
        assertNotNull(assistantMessage.getProviderTrace());
    }
    
    @Test
    void testSendTextMessage_ConversationNotFound() {
        // Arrange
        when(conversationRepository.selectById(anyLong())).thenReturn(null);
        
        SendTextMessageRequest request = SendTextMessageRequest.builder()
                .text("Hello")
                .build();
        
        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> service.sendTextMessage(999L, request, 1L));
        
        assertTrue(exception.getMessage().contains("Conversation not found"));
    }
}
