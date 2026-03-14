package com.ailearningassistant.ai.service.impl;

import com.ailearningassistant.ai.config.AppAiProperties;
import com.ailearningassistant.ai.payload.PracticeOptionPayload;
import com.ailearningassistant.ai.payload.PracticePayload;
import com.ailearningassistant.ai.payload.PracticeQuestionPayload;
import com.ailearningassistant.ai.prompt.AiPrompt;
import com.ailearningassistant.ai.service.AiPracticeService;
import com.ailearningassistant.ai.support.AiStructuredResponseParser;
import com.ailearningassistant.ai.support.AiTextFormatter;
import com.ailearningassistant.ai.support.SpringAiExecutor;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.practice.model.AiPracticeResult;
import com.ailearningassistant.practice.model.GeneratedPracticeQuestion;
import com.ailearningassistant.practice.model.PracticeOption;
import com.ailearningassistant.practice.model.QuestionType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SpringAiPracticeService implements AiPracticeService {

    private final SpringAiExecutor springAiExecutor;
    private final AiStructuredResponseParser aiStructuredResponseParser;
    private final AiTextFormatter aiTextFormatter;
    private final AppAiProperties appAiProperties;

    @Override
    public AiPracticeResult generatePractice(LearningDocument document, List<String> chunks, int questionCount) {
        try {
            String rawContent = springAiExecutor.call(
                    AiPrompt.PRACTICE_SYSTEM,
                    Map.of(),
                    AiPrompt.PRACTICE_USER,
                    Map.of(
                            "documentTitle", document.getTitle(),
                            "questionCount", questionCount,
                            "chunkText", aiTextFormatter.formatChunks(chunks, appAiProperties.getPracticeChunkLimit(), 1000),
                            "formatInstructions", aiStructuredResponseParser.formatInstructions(PracticePayload.class)
                    )
            );

            PracticePayload payload = aiStructuredResponseParser.parse(rawContent, PracticePayload.class);
            List<GeneratedPracticeQuestion> questions = payload.getQuestions() == null
                    ? Collections.emptyList()
                    : payload.getQuestions().stream().map(this::toGeneratedQuestion).toList();

            if (questions.isEmpty()) {
                throw new BusinessException(StatusCode.PRACTICE_GENERATE_FAILED.getCode(),
                        "AI returned no practice questions");
            }

            return AiPracticeResult.builder()
                    .title(StringUtils.hasText(payload.getTitle()) ? payload.getTitle().trim() : document.getTitle() + " Practice")
                    .questions(questions)
                    .build();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.PRACTICE_GENERATE_FAILED.getCode(),
                    "AI practice generation failed: " + ex.getMessage());
        }
    }

    private GeneratedPracticeQuestion toGeneratedQuestion(PracticeQuestionPayload payload) {
        QuestionType questionType;
        try {
            questionType = QuestionType.valueOf(payload.getQuestionType());
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.PRACTICE_TYPE_UNSUPPORTED.getCode(),
                    "Unsupported AI question type: " + payload.getQuestionType());
        }

        List<PracticeOption> options = payload.getOptions() == null
                ? Collections.emptyList()
                : payload.getOptions().stream().map(this::toPracticeOption).toList();

        return GeneratedPracticeQuestion.builder()
                .questionType(questionType)
                .stem(payload.getStem())
                .options(options)
                .correctAnswer(payload.getCorrectAnswer())
                .explanation(payload.getExplanation())
                .score(payload.getScore())
                .sortOrder(payload.getSortOrder())
                .build();
    }

    private PracticeOption toPracticeOption(PracticeOptionPayload payload) {
        return PracticeOption.builder()
                .key(payload.getKey())
                .content(payload.getContent())
                .build();
    }
}
