package com.kaelenx.conversation.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Message entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("message")
public class Message {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * Associated conversation ID
     */
    private Long conversationId;
    
    /**
     * Sender role (USER, ASSISTANT)
     */
    private String senderRole;
    
    /**
     * Content type (TEXT, IMAGE, AUDIO)
     */
    private String contentType;
    
    /**
     * Text content of the message
     */
    private String textContent;
    
    /**
     * Message status (PENDING, FINAL, FAILED)
     */
    private String status;
    
    /**
     * Provider trace information (JSON string)
     */
    private String providerTrace;
    
    /**
     * Soft delete timestamp
     */
    private LocalDateTime deletedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
