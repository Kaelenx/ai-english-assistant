package com.kaelenx.aiorchestrator.controller;

import com.kaelenx.aiorchestrator.dto.ChatRequest;
import com.kaelenx.aiorchestrator.dto.ChatResponse;
import com.kaelenx.aiorchestrator.service.AiOrchestratorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal API controller for AI orchestration.
 * These endpoints are intended to be called by other internal services.
 */
@RestController
@RequestMapping("/internal/ai")
@RequiredArgsConstructor
public class InternalAiController {
    
    private final AiOrchestratorService aiOrchestratorService;
    
    /**
     * Process a chat request and return AI completion
     */
    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = aiOrchestratorService.chat(request);
        return ResponseEntity.ok(response);
    }
}
