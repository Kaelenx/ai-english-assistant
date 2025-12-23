package com.kaelenx.common.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EventEnvelopeTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    
    @Test
    void testEventEnvelopeCreation() {
        String testPayload = "test payload";
        EventEnvelope<String> envelope = EventEnvelope.of("test.event", testPayload);
        
        assertNotNull(envelope);
        assertEquals("test.event", envelope.getEventType());
        assertEquals(testPayload, envelope.getPayload());
        assertEquals("1.0", envelope.getVersion());
        assertNotNull(envelope.getTimestamp());
    }
    
    @Test
    void testEventEnvelopeBuilder() {
        Map<String, String> metadata = new HashMap<>();
        metadata.put("userId", "user123");
        metadata.put("correlationId", "corr456");
        
        EventEnvelope<String> envelope = EventEnvelope.<String>builder()
                .eventId("evt123")
                .eventType(EventTypes.CONVERSATION_CREATED)
                .source("conversation-service")
                .timestamp(Instant.now())
                .version("1.0")
                .payload("test data")
                .metadata(metadata)
                .build();
        
        assertEquals("evt123", envelope.getEventId());
        assertEquals(EventTypes.CONVERSATION_CREATED, envelope.getEventType());
        assertEquals("conversation-service", envelope.getSource());
        assertEquals("test data", envelope.getPayload());
        assertEquals(2, envelope.getMetadata().size());
    }
    
    @Test
    void testJsonSerialization() throws Exception {
        EventEnvelope<Map<String, Object>> envelope = EventEnvelope.<Map<String, Object>>builder()
                .eventId("evt123")
                .eventType(EventTypes.MESSAGE_SENT)
                .source("conversation-service")
                .timestamp(Instant.parse("2024-01-01T00:00:00Z"))
                .version("1.0")
                .payload(Map.of("messageId", 123L, "text", "Hello"))
                .build();
        
        String json = objectMapper.writeValueAsString(envelope);
        assertNotNull(json);
        assertTrue(json.contains("evt123"));
        assertTrue(json.contains("message.sent"));
        
        EventEnvelope<?> deserialized = objectMapper.readValue(json, EventEnvelope.class);
        assertEquals(envelope.getEventId(), deserialized.getEventId());
        assertEquals(envelope.getEventType(), deserialized.getEventType());
    }
}
