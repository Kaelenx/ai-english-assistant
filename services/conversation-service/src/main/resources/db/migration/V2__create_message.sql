-- Message Table
-- Stores all messages in conversations

CREATE TABLE message (
    id BIGINT PRIMARY KEY COMMENT 'Snowflake ID',
    conversation_id BIGINT NOT NULL COMMENT 'Associated conversation ID',
    sender_role VARCHAR(20) NOT NULL COMMENT 'Sender role (USER, ASSISTANT)',
    content_type VARCHAR(20) NOT NULL COMMENT 'Content type (TEXT, IMAGE, AUDIO)',
    text_content TEXT COMMENT 'Text content of the message',
    status VARCHAR(20) NOT NULL COMMENT 'Message status (PENDING, FINAL, FAILED)',
    provider_trace TEXT COMMENT 'Provider trace information (JSON)',
    deleted_at TIMESTAMP NULL COMMENT 'Soft delete timestamp',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Creation timestamp',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'Last update timestamp',
    INDEX idx_conversation_id (conversation_id),
    INDEX idx_sender_role (sender_role),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (conversation_id) REFERENCES conversation(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Messages in conversations';
