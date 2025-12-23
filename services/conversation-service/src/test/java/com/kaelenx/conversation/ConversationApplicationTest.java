package com.kaelenx.conversation;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConversationApplicationTest {
    
    @Test
    void testMainClassExists() {
        // Smoke test to verify main class exists and can be instantiated
        assertDoesNotThrow(() -> {
            ConversationApplication app = new ConversationApplication();
            assertNotNull(app);
        });
    }
}
