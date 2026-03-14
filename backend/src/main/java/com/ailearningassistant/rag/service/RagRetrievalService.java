package com.ailearningassistant.rag.service;

import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import java.util.List;

public interface RagRetrievalService {

    List<ChunkReferenceVO> retrieveRelevantChunks(Long documentId, Long userId, String question);
}
