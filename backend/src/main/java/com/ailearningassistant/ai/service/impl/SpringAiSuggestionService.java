package com.ailearningassistant.ai.service.impl;

import com.ailearningassistant.ai.config.AppAiProperties;
import com.ailearningassistant.ai.payload.SuggestionPayload;
import com.ailearningassistant.ai.prompt.AiPrompt;
import com.ailearningassistant.ai.service.AiSuggestionService;
import com.ailearningassistant.ai.support.AiStructuredResponseParser;
import com.ailearningassistant.ai.support.AiTextFormatter;
import com.ailearningassistant.ai.support.SpringAiExecutor;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.report.vo.KnowledgePointStatVO;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SpringAiSuggestionService implements AiSuggestionService {

    private final SpringAiExecutor springAiExecutor;
    private final AiStructuredResponseParser aiStructuredResponseParser;
    private final AiTextFormatter aiTextFormatter;
    private final AppAiProperties appAiProperties;

    @Override
    public List<String> generateSuggestions(int documentCount,
                                            int qaCount,
                                            int practiceCount,
                                            BigDecimal accuracy,
                                            List<KnowledgePointStatVO> wrongKnowledgePoints) {
        try {
            String rawContent = springAiExecutor.call(
                    AiPrompt.SUGGESTION_SYSTEM,
                    Map.of(),
                    AiPrompt.SUGGESTION_USER,
                    Map.of(
                            "documentCount", documentCount,
                            "qaCount", qaCount,
                            "practiceCount", practiceCount,
                            "accuracy", accuracy,
                            "wrongKnowledgePoints",
                            aiTextFormatter.formatKnowledgePoints(wrongKnowledgePoints, appAiProperties.getSuggestionWrongPointLimit()),
                            "formatInstructions", aiStructuredResponseParser.formatInstructions(SuggestionPayload.class)
                    )
            );

            SuggestionPayload payload = aiStructuredResponseParser.parse(rawContent, SuggestionPayload.class);
            return payload.getSuggestions() == null
                    ? Collections.emptyList()
                    : payload.getSuggestions().stream().filter(StringUtils::hasText).map(String::trim).toList();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.REPORT_GENERATE_FAILED.getCode(),
                    "AI suggestion generation failed: " + ex.getMessage());
        }
    }
}
