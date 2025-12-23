package com.kaelenx.aiorchestrator.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI request log entity for tracking all AI service calls
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("ai_request_log")
public class AiRequestLog {
    
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    
    private Long conversationId;
    
    private Long userId;
    
    private Long sceneId;
    
    private String difficulty;
    
    private String planTier;
    
    /**
     * Provider name (e.g., "qwen", "mock")
     */
    private String provider;
    
    /**
     * Model name (e.g., "qwen-turbo")
     */
    private String model;
    
    /**
     * Request status (e.g., "SUCCESS", "FAILED")
     */
    private String status;
    
    /**
     * Input tokens (nullable for mock responses)
     */
    private Integer tokenIn;
    
    /**
     * Output tokens (nullable for mock responses)
     */
    private Integer tokenOut;
    
    /**
     * Latency in milliseconds
     */
    private Long latencyMs;
    
    /**
     * Error message if request failed
     */
    private String errorMessage;
    
    private LocalDateTime createdAt;
}
