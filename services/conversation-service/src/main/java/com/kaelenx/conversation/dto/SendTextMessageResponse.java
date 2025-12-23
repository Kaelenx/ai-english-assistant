package com.kaelenx.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response for text message sending
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendTextMessageResponse {
    
    private Long userMessageId;
    
    private Long assistantMessageId;
    
    private String replyText;
}
