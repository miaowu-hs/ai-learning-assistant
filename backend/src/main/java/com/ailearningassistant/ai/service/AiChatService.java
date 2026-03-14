package com.ailearningassistant.ai.service;

import com.ailearningassistant.ai.model.AiChatResult;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import java.util.List;

public interface AiChatService {

    AiChatResult generateAnswer(LearningDocument document, String question, String prompt, List<ChunkReferenceVO> references);
}
