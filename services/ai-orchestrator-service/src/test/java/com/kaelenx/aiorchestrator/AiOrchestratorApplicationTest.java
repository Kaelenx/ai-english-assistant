package com.kaelenx.aiorchestrator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AiOrchestratorApplicationTest {
    
    @Test
    void testMainClassExists() {
        // Smoke test to verify main class exists and can be instantiated
        assertDoesNotThrow(() -> {
            AiOrchestratorApplication app = new AiOrchestratorApplication();
            assertNotNull(app);
        });
    }
}
