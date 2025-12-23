package com.kaelenx.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for conversation creation
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateConversationResponse {
    
    private Long conversationId;
    
    private String status;
}
