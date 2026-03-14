package com.ailearningassistant.rag.service;

import com.ailearningassistant.rag.dto.AskQuestionRequest;
import com.ailearningassistant.rag.dto.CreateQaSessionRequest;
import com.ailearningassistant.rag.vo.QaAskResponseVO;
import com.ailearningassistant.rag.vo.QaSessionHistoryVO;
import com.ailearningassistant.rag.vo.QaSessionVO;

public interface RagQaService {

    QaSessionVO createSession(CreateQaSessionRequest request);

    QaAskResponseVO askQuestion(Long sessionId, AskQuestionRequest request);

    QaSessionHistoryVO getSessionHistory(Long sessionId);
}
