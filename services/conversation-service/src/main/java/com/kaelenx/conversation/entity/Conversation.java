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
 * Conversation entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("conversation")
public class Conversation {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    /**
     * User ID (currently fixed to 1 or from header)
     */
    private Long userId;
    
    /**
     * Scene/scenario ID for the conversation
     */
    private Long sceneId;
    
    /**
     * Difficulty level (e.g., EASY, MEDIUM, HARD)
     */
    private String difficulty;
    
    /**
     * Conversation status (e.g., ACTIVE, ENDED)
     */
    private String status;
    
    /**
     * User's plan tier (e.g., FREE, PREMIUM)
     */
    private String planTier;
    
    /**
     * Soft delete timestamp
     */
    private LocalDateTime deletedAt;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
}
