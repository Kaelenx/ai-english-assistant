package com.kaelenx.common.id;

import lombok.extern.slf4j.Slf4j;

/**
 * Snowflake ID generator for distributed systems.
 * 
 * Structure (64 bits):
 * - 1 bit: unused (always 0)
 * - 41 bits: timestamp (milliseconds since custom epoch)
 * - 10 bits: worker ID (0-1023)
 * - 12 bits: sequence number (0-4095)
 * 
 * This generator can produce up to 4096 unique IDs per millisecond per worker.
 */
@Slf4j
public class SnowflakeIdGenerator {
    
    // Custom epoch (2024-01-01 00:00:00 UTC)
    private static final long CUSTOM_EPOCH = 1704067200000L;
    
    // Bit lengths
    private static final long WORKER_ID_BITS = 10L;
    private static final long SEQUENCE_BITS = 12L;
    
    // Max values
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    // Bit shifts
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    
    private final long workerId;
    private long sequence = 0L;
    private long lastTimestamp = -1L;
    
    /**
     * Creates a new Snowflake ID generator.
     * 
     * @param workerId Worker ID (0-1023)
     * @throws IllegalArgumentException if workerId is out of range
     */
    public SnowflakeIdGenerator(long workerId) {
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException(
                String.format("Worker ID must be between 0 and %d", MAX_WORKER_ID));
        }
        this.workerId = workerId;
        log.info("SnowflakeIdGenerator initialized with workerId={}", workerId);
    }
    
    /**
     * Generates a new unique ID.
     * 
     * @return Unique 64-bit ID
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();
        
        // Clock moved backwards - wait until it's back
        if (timestamp < lastTimestamp) {
            long offset = lastTimestamp - timestamp;
            if (offset > 5) {
                throw new RuntimeException(
                    String.format("Clock moved backwards by %d ms. Refusing to generate ID", offset));
            }
            try {
                Thread.sleep(offset << 1);
                timestamp = currentTimeMillis();
                if (timestamp < lastTimestamp) {
                    throw new RuntimeException("Clock moved backwards. Refusing to generate ID");
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for clock to catch up", e);
            }
        }
        
        // Same millisecond - increment sequence
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // Sequence overflow - wait for next millisecond
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // New millisecond - reset sequence
            sequence = 0L;
        }
        
        lastTimestamp = timestamp;
        
        // Generate and return ID
        return ((timestamp - CUSTOM_EPOCH) << TIMESTAMP_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }
    
    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
    
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }
    
    /**
     * Extracts the timestamp component from a Snowflake ID.
     * 
     * @param id Snowflake ID
     * @return Timestamp in milliseconds
     */
    public static long extractTimestamp(long id) {
        return (id >> TIMESTAMP_SHIFT) + CUSTOM_EPOCH;
    }
    
    /**
     * Extracts the worker ID component from a Snowflake ID.
     * 
     * @param id Snowflake ID
     * @return Worker ID
     */
    public static long extractWorkerId(long id) {
        return (id >> WORKER_ID_SHIFT) & MAX_WORKER_ID;
    }
    
    /**
     * Extracts the sequence component from a Snowflake ID.
     * 
     * @param id Snowflake ID
     * @return Sequence number
     */
    public static long extractSequence(long id) {
        return id & MAX_SEQUENCE;
    }
}
