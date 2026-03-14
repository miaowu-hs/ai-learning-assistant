package com.ailearningassistant.ai.service.impl;

import com.ailearningassistant.ai.config.AppAiProperties;
import com.ailearningassistant.ai.model.AiSummaryResult;
import com.ailearningassistant.ai.payload.SummaryPayload;
import com.ailearningassistant.ai.prompt.AiPrompt;
import com.ailearningassistant.ai.service.AiSummaryService;
import com.ailearningassistant.ai.support.AiStructuredResponseParser;
import com.ailearningassistant.ai.support.AiTextFormatter;
import com.ailearningassistant.ai.support.SpringAiExecutor;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.document.entity.LearningDocument;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SpringAiSummaryService implements AiSummaryService {

    private final SpringAiExecutor springAiExecutor;
    private final AiStructuredResponseParser aiStructuredResponseParser;
    private final AiTextFormatter aiTextFormatter;
    private final AppAiProperties appAiProperties;

    @Override
    public AiSummaryResult generateSummary(LearningDocument document, List<String> chunks) {
        try {
            String rawContent = springAiExecutor.call(
                    AiPrompt.SUMMARY_SYSTEM,
                    Map.of(),
                    AiPrompt.SUMMARY_USER,
                    Map.of(
                            "documentTitle", document.getTitle(),
                            "fileType", document.getFileType(),
                            "chunkText", aiTextFormatter.formatChunks(chunks, appAiProperties.getSummaryChunkLimit(), 1200),
                            "formatInstructions", aiStructuredResponseParser.formatInstructions(SummaryPayload.class)
                    )
            );

            SummaryPayload payload = aiStructuredResponseParser.parse(rawContent, SummaryPayload.class);
            return AiSummaryResult.builder()
                    .shortSummary(StringUtils.hasText(payload.getShortSummary())
                            ? payload.getShortSummary().trim()
                            : "No summary generated.")
                    .outline(defaultList(payload.getOutline()))
                    .keyPoints(defaultList(payload.getKeyPoints()))
                    .build();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.DOCUMENT_SUMMARY_FAILED.getCode(),
                    "AI summary generation failed: " + ex.getMessage());
        }
    }

    private List<String> defaultList(List<String> values) {
        return values == null ? Collections.emptyList() : values.stream().filter(StringUtils::hasText).map(String::trim).toList();
    }
}
