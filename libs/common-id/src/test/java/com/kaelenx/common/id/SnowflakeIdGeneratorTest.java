package com.kaelenx.common.id;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class SnowflakeIdGeneratorTest {
    
    @Test
    void testGenerateUniqueIds() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);
        Set<Long> ids = new HashSet<>();
        
        for (int i = 0; i < 10000; i++) {
            long id = generator.nextId();
            assertTrue(id > 0, "ID should be positive");
            assertTrue(ids.add(id), "ID should be unique");
        }
        
        assertEquals(10000, ids.size(), "Should generate 10000 unique IDs");
    }
    
    @Test
    void testWorkerIdValidation() {
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(-1));
        assertThrows(IllegalArgumentException.class, () -> new SnowflakeIdGenerator(1024));
        assertDoesNotThrow(() -> new SnowflakeIdGenerator(0));
        assertDoesNotThrow(() -> new SnowflakeIdGenerator(1023));
    }
    
    @Test
    void testIdComponentExtraction() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(42);
        long id = generator.nextId();
        
        long workerId = SnowflakeIdGenerator.extractWorkerId(id);
        assertEquals(42, workerId, "Extracted worker ID should match");
        
        long timestamp = SnowflakeIdGenerator.extractTimestamp(id);
        assertTrue(timestamp > 0, "Extracted timestamp should be positive");
        assertTrue(timestamp <= System.currentTimeMillis(), "Timestamp should not be in the future");
        
        long sequence = SnowflakeIdGenerator.extractSequence(id);
        assertTrue(sequence >= 0 && sequence < 4096, "Sequence should be in valid range");
    }
    
    @Test
    void testConcurrentGeneration() throws InterruptedException {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);
        int threadCount = 10;
        int idsPerThread = 1000;
        
        Set<Long> allIds = new HashSet<>();
        CountDownLatch latch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        AtomicInteger duplicates = new AtomicInteger(0);
        
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                Set<Long> threadIds = new HashSet<>();
                for (int j = 0; j < idsPerThread; j++) {
                    long id = generator.nextId();
                    threadIds.add(id);
                }
                
                synchronized (allIds) {
                    for (Long id : threadIds) {
                        if (!allIds.add(id)) {
                            duplicates.incrementAndGet();
                        }
                    }
                }
                latch.countDown();
            });
        }
        
        latch.await();
        executor.shutdown();
        
        assertEquals(0, duplicates.get(), "Should have no duplicate IDs");
        assertEquals(threadCount * idsPerThread, allIds.size(), 
            "Should generate expected number of unique IDs");
    }
    
    @Test
    void testIdOrder() {
        SnowflakeIdGenerator generator = new SnowflakeIdGenerator(1);
        long id1 = generator.nextId();
        long id2 = generator.nextId();
        long id3 = generator.nextId();
        
        assertTrue(id1 < id2, "IDs should be monotonically increasing");
        assertTrue(id2 < id3, "IDs should be monotonically increasing");
    }
}
