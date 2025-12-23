package com.kaelenx.aiorchestrator.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaelenx.aiorchestrator.entity.AiRequestLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * Repository for AI request logs
 */
@Mapper
public interface AiRequestLogRepository extends BaseMapper<AiRequestLog> {
}
