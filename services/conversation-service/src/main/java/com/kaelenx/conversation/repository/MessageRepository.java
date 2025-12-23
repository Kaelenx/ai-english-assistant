package com.kaelenx.conversation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaelenx.conversation.entity.Message;
import org.apache.ibatis.annotations.Mapper;

/**
 * Repository for Message entities
 */
@Mapper
public interface MessageRepository extends BaseMapper<Message> {
}
