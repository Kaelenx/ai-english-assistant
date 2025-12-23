package com.kaelenx.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to create a new conversation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationRequest {
    
    @NotNull(message = "sceneId is required")
    private Long sceneId;
    
    @NotBlank(message = "difficulty is required")
    private String difficulty;
    
    @NotBlank(message = "planTier is required")
    private String planTier;
}
