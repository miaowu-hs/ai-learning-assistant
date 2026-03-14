package com.ailearningassistant.rag.mapper;

import com.ailearningassistant.rag.entity.QaMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QaMessageMapper extends BaseMapper<QaMessage> {
}
