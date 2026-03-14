package com.ailearningassistant.ai.service;

import com.ailearningassistant.ai.model.AiSummaryResult;
import com.ailearningassistant.document.entity.LearningDocument;
import java.util.List;

public interface AiSummaryService {

    AiSummaryResult generateSummary(LearningDocument document, List<String> chunks);
}
