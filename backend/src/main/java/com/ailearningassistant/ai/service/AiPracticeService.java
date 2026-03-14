package com.ailearningassistant.ai.service;

import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.practice.model.AiPracticeResult;
import java.util.List;

public interface AiPracticeService {

    AiPracticeResult generatePractice(LearningDocument document, List<String> chunks, int questionCount);
}
