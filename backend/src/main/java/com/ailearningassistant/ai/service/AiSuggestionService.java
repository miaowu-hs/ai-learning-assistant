package com.ailearningassistant.ai.service;

import com.ailearningassistant.report.vo.KnowledgePointStatVO;
import java.math.BigDecimal;
import java.util.List;

public interface AiSuggestionService {

    List<String> generateSuggestions(int documentCount,
                                     int qaCount,
                                     int practiceCount,
                                     BigDecimal accuracy,
                                     List<KnowledgePointStatVO> wrongKnowledgePoints);
}
