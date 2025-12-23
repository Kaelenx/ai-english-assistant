-- Conversation Table
-- Stores conversation metadata and settings

CREATE TABLE conversation (
    id BIGINT PRIMARY KEY COMMENT 'Snowflake ID',
    user_id BIGINT NOT NULL COMMENT 'User ID',
    scene_id BIGINT NOT NULL COMMENT 'Scene/scenario ID',
    difficulty VARCHAR(50) NOT NULL COMMENT 'Difficulty level (EASY, MEDIUM, HARD)',
    status VARCHAR(20) NOT NULL COMMENT 'Conversation status (ACTIVE, ENDED)',
    plan_tier VARCHAR(50) NOT NULL COMMENT 'User plan tier (FREE, PREMIUM)',
    deleted_at TIMESTAMP NULL COMMENT 'Soft delete timestamp',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Conversation metadata and settings';
