package com.ailearningassistant.rag.service;

import com.ailearningassistant.rag.entity.QaMessage;
import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import com.ailearningassistant.rag.vo.QaMessageVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface QaMessageService extends IService<QaMessage> {

    QaMessage saveMessage(Long sessionId, Long userId, String role, String content, List<ChunkReferenceVO> references);

    List<QaMessageVO> listSessionMessages(Long sessionId, Long userId);
}
