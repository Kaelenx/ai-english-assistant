package com.kaelenx.common.id;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Snowflake ID generator.
 */
@Configuration
public class IdGeneratorConfig {
    
    @Bean
    public SnowflakeIdGenerator snowflakeIdGenerator(
            @Value("${id.generator.worker-id:0}") long workerId) {
        return new SnowflakeIdGenerator(workerId);
    }
}
