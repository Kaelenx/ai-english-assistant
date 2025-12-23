package com.kaelenx.common.events;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

/**
 * Generic event envelope for message queue integration.
 * This structure provides a consistent format for all events in the system.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EventEnvelope<T> {
    
    /**
     * Unique event ID
     */
    private String eventId;
    
    /**
     * Event type (e.g., "conversation.created", "message.sent")
     */
    private String eventType;
    
    /**
     * Source service that produced the event
     */
    private String source;
    
    /**
     * Timestamp when the event was created
     */
    private Instant timestamp;
    
    /**
     * Version of the event schema
     */
    private String version;
    
    /**
     * The actual event payload
     */
    private T payload;
    
    /**
     * Additional metadata (correlation ID, user ID, etc.)
     */
    private Map<String, String> metadata;
    
    /**
     * Creates an event envelope with the given payload and type.
     */
    public static <T> EventEnvelope<T> of(String eventType, T payload) {
        return EventEnvelope.<T>builder()
                .eventType(eventType)
                .payload(payload)
                .timestamp(Instant.now())
                .version("1.0")
                .build();
    }
}
