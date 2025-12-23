package com.kaelenx.conversation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request to send a text message
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTextMessageRequest {
    
    @NotBlank(message = "text is required")
    private String text;
}
