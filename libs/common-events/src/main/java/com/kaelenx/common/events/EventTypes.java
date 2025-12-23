package com.kaelenx.common.events;

/**
 * Constants for event types used across the system.
 * These will be used when integrating with message queues in the future.
 */
public final class EventTypes {
    
    private EventTypes() {
        // Utility class - prevent instantiation
    }
    
    // Conversation events
    public static final String CONVERSATION_CREATED = "conversation.created";
    public static final String CONVERSATION_UPDATED = "conversation.updated";
    public static final String CONVERSATION_DELETED = "conversation.deleted";
    
    // Message events
    public static final String MESSAGE_SENT = "message.sent";
    public static final String MESSAGE_RECEIVED = "message.received";
    
    // AI orchestrator events
    public static final String AI_REQUEST_INITIATED = "ai.request.initiated";
    public static final String AI_REQUEST_COMPLETED = "ai.request.completed";
    public static final String AI_REQUEST_FAILED = "ai.request.failed";
}
