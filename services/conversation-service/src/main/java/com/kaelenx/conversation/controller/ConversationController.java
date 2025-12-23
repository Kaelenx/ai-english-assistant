package com.kaelenx.conversation.controller;

import com.kaelenx.conversation.dto.*;
import com.kaelenx.conversation.service.ConversationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST API controller for conversations
 */
@RestController
@RequestMapping("/v1/conversations")
@RequiredArgsConstructor
public class ConversationController {
    
    private final ConversationService conversationService;
    
    /**
     * Create a new conversation
     */
    @PostMapping
    public ResponseEntity<CreateConversationResponse> createConversation(
            @Valid @RequestBody CreateConversationRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        CreateConversationResponse response = conversationService.createConversation(request, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Send a text message in a conversation
     */
    @PostMapping("/{conversationId}/messages:text")
    public ResponseEntity<SendTextMessageResponse> sendTextMessage(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendTextMessageRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId) {
        SendTextMessageResponse response = conversationService.sendTextMessage(conversationId, request, userId);
        return ResponseEntity.ok(response);
    }
}
