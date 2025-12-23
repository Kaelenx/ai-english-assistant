-- AI Request Log Table
-- Tracks all AI service calls for billing/usage statistics and debugging

CREATE TABLE ai_request_log (
    id BIGINT PRIMARY KEY COMMENT 'Snowflake ID',
    conversation_id BIGINT NOT NULL COMMENT 'Conversation ID from conversation service',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    scene_id BIGINT NOT NULL COMMENT 'Scene/scenario ID',
    difficulty VARCHAR(50) NOT NULL COMMENT 'Difficulty level',
    plan_tier VARCHAR(50) NOT NULL COMMENT 'User plan tier',
    provider VARCHAR(50) NOT NULL COMMENT 'LLM provider name (e.g., qwen, openai)',
    model VARCHAR(100) NOT NULL COMMENT 'Model name (e.g., qwen-turbo)',
    status VARCHAR(20) NOT NULL COMMENT 'Request status (SUCCESS, FAILED)',
    token_in INT COMMENT 'Input tokens consumed (nullable for mock)',
    token_out INT COMMENT 'Output tokens consumed (nullable for mock)',
    latency_ms BIGINT COMMENT 'Request latency in milliseconds',
    error_message TEXT COMMENT 'Error message if request failed',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_user_id (user_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='AI request log for tracking and billing';
