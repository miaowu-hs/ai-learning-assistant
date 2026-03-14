package com.ailearningassistant.rag.service;

import com.ailearningassistant.rag.dto.CreateQaSessionRequest;
import com.ailearningassistant.rag.entity.QaSession;
import com.ailearningassistant.rag.vo.QaSessionVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface QaSessionService extends IService<QaSession> {

    QaSessionVO createMySession(CreateQaSessionRequest request);

    QaSession getMySessionEntity(Long sessionId);

    void touchSession(Long sessionId);
}
