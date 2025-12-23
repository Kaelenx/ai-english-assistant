package com.kaelenx.conversation.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.kaelenx.conversation.entity.Conversation;
import org.apache.ibatis.annotations.Mapper;

/**
 * Repository for Conversation entities
 */
@Mapper
public interface ConversationRepository extends BaseMapper<Conversation> {
}
