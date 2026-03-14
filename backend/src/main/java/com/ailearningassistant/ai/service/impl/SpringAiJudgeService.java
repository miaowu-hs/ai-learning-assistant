package com.ailearningassistant.ai.service.impl;

import com.ailearningassistant.ai.payload.JudgePayload;
import com.ailearningassistant.ai.prompt.AiPrompt;
import com.ailearningassistant.ai.service.AiJudgeService;
import com.ailearningassistant.ai.support.AiStructuredResponseParser;
import com.ailearningassistant.ai.support.SpringAiExecutor;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.practice.entity.PracticeQuestion;
import com.ailearningassistant.practice.model.AiJudgeResult;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class SpringAiJudgeService implements AiJudgeService {

    private final SpringAiExecutor springAiExecutor;
    private final AiStructuredResponseParser aiStructuredResponseParser;

    @Override
    public AiJudgeResult judgeShortAnswer(PracticeQuestion question, String userAnswer) {
        if (!StringUtils.hasText(userAnswer)) {
            return AiJudgeResult.builder()
                    .correct(false)
                    .score(0)
                    .analysis("No answer provided.")
                    .build();
        }

        try {
            String rawContent = springAiExecutor.call(
                    AiPrompt.JUDGE_SYSTEM,
                    Map.of(),
                    AiPrompt.JUDGE_USER,
                    Map.of(
                            "stem", question.getStem(),
                            "correctAnswer", question.getCorrectAnswer(),
                            "explanation", question.getExplanation(),
                            "maxScore", question.getScore(),
                            "userAnswer", userAnswer,
                            "formatInstructions", aiStructuredResponseParser.formatInstructions(JudgePayload.class)
                    )
            );

            JudgePayload payload = aiStructuredResponseParser.parse(rawContent, JudgePayload.class);
            int maxScore = question.getScore() == null ? 20 : question.getScore();
            int score = payload.getScore() == null ? 0 : Math.max(0, Math.min(payload.getScore(), maxScore));
            return AiJudgeResult.builder()
                    .correct(Boolean.TRUE.equals(payload.getCorrect()))
                    .score(score)
                    .analysis(StringUtils.hasText(payload.getAnalysis()) ? payload.getAnalysis().trim() : "No analysis provided.")
                    .build();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.PRACTICE_JUDGE_FAILED.getCode(),
                    "AI judge failed: " + ex.getMessage());
        }
    }
}
