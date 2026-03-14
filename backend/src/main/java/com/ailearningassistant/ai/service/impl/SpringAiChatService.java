package com.ailearningassistant.ai.service.impl;

import com.ailearningassistant.ai.model.AiChatResult;
import com.ailearningassistant.ai.prompt.AiPrompt;
import com.ailearningassistant.ai.service.AiChatService;
import com.ailearningassistant.ai.support.AiTextFormatter;
import com.ailearningassistant.ai.support.SpringAiExecutor;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SpringAiChatService implements AiChatService {

    private final SpringAiExecutor springAiExecutor;
    private final AiTextFormatter aiTextFormatter;

    @Override
    public AiChatResult generateAnswer(LearningDocument document,
                                       String question,
                                       String prompt,
                                       List<ChunkReferenceVO> references) {
        try {
            String answer = springAiExecutor.call(
                    AiPrompt.CHAT_SYSTEM,
                    Map.of(),
                    AiPrompt.CHAT_USER,
                    Map.of(
                            "documentTitle", document.getTitle(),
                            "question", question,
                            "referencesText", aiTextFormatter.formatReferences(references, 6, 1200),
                            "promptContext", StringUtils.hasText(prompt) ? prompt : "None"
                    )
            );
            return AiChatResult.builder()
                    .answer(StringUtils.hasText(answer) ? answer.trim() : "No answer generated.")
                    .build();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.QA_GENERATE_FAILED.getCode(),
                    "AI answer generation failed: " + ex.getMessage());
        }
    }
}
