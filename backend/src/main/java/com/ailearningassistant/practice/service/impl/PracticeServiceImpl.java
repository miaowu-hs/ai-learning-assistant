package com.ailearningassistant.practice.service.impl;

import com.ailearningassistant.ai.service.AiJudgeService;
import com.ailearningassistant.ai.service.AiPracticeService;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.document.entity.DocumentChunk;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.service.DocumentChunkService;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.practice.dto.GeneratePracticeRequest;
import com.ailearningassistant.practice.dto.SubmitPracticeAnswerItem;
import com.ailearningassistant.practice.dto.SubmitPracticeRequest;
import com.ailearningassistant.practice.entity.PracticeAnswer;
import com.ailearningassistant.practice.entity.PracticeQuestion;
import com.ailearningassistant.practice.entity.PracticeRecord;
import com.ailearningassistant.practice.entity.PracticeSet;
import com.ailearningassistant.practice.model.AiJudgeResult;
import com.ailearningassistant.practice.model.AiPracticeResult;
import com.ailearningassistant.practice.model.GeneratedPracticeQuestion;
import com.ailearningassistant.practice.model.PracticeOption;
import com.ailearningassistant.practice.model.QuestionType;
import com.ailearningassistant.practice.service.PracticeAnswerService;
import com.ailearningassistant.practice.service.PracticeQuestionService;
import com.ailearningassistant.practice.service.PracticeRecordService;
import com.ailearningassistant.practice.service.PracticeService;
import com.ailearningassistant.practice.service.PracticeSetService;
import com.ailearningassistant.practice.vo.PracticeAnswerResultVO;
import com.ailearningassistant.practice.vo.PracticeOptionVO;
import com.ailearningassistant.practice.vo.PracticeQuestionVO;
import com.ailearningassistant.practice.vo.PracticeResultVO;
import com.ailearningassistant.practice.vo.PracticeSetDetailVO;
import com.ailearningassistant.wrongbook.service.WrongQuestionBookService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class PracticeServiceImpl implements PracticeService {

    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_SUBMITTED = "SUBMITTED";

    private final LearningDocumentService learningDocumentService;
    private final DocumentChunkService documentChunkService;
    private final PracticeSetService practiceSetService;
    private final PracticeQuestionService practiceQuestionService;
    private final PracticeRecordService practiceRecordService;
    private final PracticeAnswerService practiceAnswerService;
    private final AiPracticeService aiPracticeService;
    private final AiJudgeService aiJudgeService;
    private final ObjectMapper objectMapper;
    private final WrongQuestionBookService wrongQuestionBookService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PracticeSetDetailVO generatePractice(GeneratePracticeRequest request) {
        LearningDocument document = learningDocumentService.getMyDocumentEntity(request.getDocumentId());
        ensureDocumentReady(document);

        List<String> chunks = documentChunkService.listByDocumentId(document.getId(), document.getUserId())
                .stream()
                .map(DocumentChunk::getChunkContent)
                .toList();
        AiPracticeResult aiPracticeResult = aiPracticeService.generatePractice(document, chunks, request.getQuestionCount());
        if (aiPracticeResult == null || aiPracticeResult.getQuestions() == null || aiPracticeResult.getQuestions().isEmpty()) {
            throw new BusinessException(StatusCode.PRACTICE_GENERATE_FAILED);
        }

        LocalDateTime now = LocalDateTime.now();
        PracticeSet practiceSet = PracticeSet.builder()
                .userId(SecurityUtils.getUserId())
                .documentId(document.getId())
                .title(resolveSetTitle(request.getTitle(), aiPracticeResult.getTitle(), document.getTitle()))
                .questionCount(aiPracticeResult.getQuestions().size())
                .totalScore(aiPracticeResult.getQuestions().stream().mapToInt(item -> safeScore(item.getScore())).sum())
                .generationPayload(toJson(aiPracticeResult))
                .createdAt(now)
                .updatedAt(now)
                .build();

        if (!practiceSetService.save(practiceSet)) {
            throw new BusinessException(StatusCode.PRACTICE_GENERATE_FAILED);
        }

        List<PracticeQuestion> questions = buildQuestionEntities(practiceSet.getId(), aiPracticeResult.getQuestions(), now);
        practiceQuestionService.replaceQuestions(practiceSet.getId(), questions);
        List<PracticeQuestion> savedQuestions = practiceQuestionService.listByPracticeSetId(practiceSet.getId());
        return buildPracticeSetDetail(practiceSet, savedQuestions);
    }

    @Override
    public PracticeSetDetailVO getPracticeDetail(Long practiceSetId) {
        PracticeSet practiceSet = practiceSetService.getMyPracticeSet(practiceSetId);
        List<PracticeQuestion> questions = practiceQuestionService.listByPracticeSetId(practiceSetId);
        return buildPracticeSetDetail(practiceSet, questions);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PracticeResultVO submitPractice(Long practiceSetId, SubmitPracticeRequest request) {
        PracticeSet practiceSet = practiceSetService.getMyPracticeSet(practiceSetId);
        List<PracticeQuestion> questions = practiceQuestionService.listByPracticeSetId(practiceSetId);
        if (questions.isEmpty()) {
            throw new BusinessException(StatusCode.PRACTICE_QUESTION_NOT_FOUND);
        }

        Map<Long, String> answerMap = toAnswerMap(request.getAnswers());
        validateSubmittedQuestionIds(questions, answerMap);

        int objectiveScore = 0;
        int subjectiveScore = 0;
        Long userId = SecurityUtils.getUserId();
        List<PracticeAnswer> answerEntities = new ArrayList<>(questions.size());
        List<JudgedAnswer> judgedAnswers = new ArrayList<>(questions.size());

        for (PracticeQuestion question : questions) {
            String userAnswer = answerMap.getOrDefault(question.getId(), "");
            JudgedAnswer judgedAnswer = judge(question, userAnswer);
            judgedAnswers.add(judgedAnswer);
            if (isObjective(question)) {
                objectiveScore += judgedAnswer.score();
            } else {
                subjectiveScore += judgedAnswer.score();
            }
        }

        PracticeRecord record = practiceRecordService.createRecord(
                practiceSet.getId(),
                userId,
                objectiveScore + subjectiveScore,
                objectiveScore,
                subjectiveScore,
                STATUS_SUBMITTED
        );

        LocalDateTime now = LocalDateTime.now();
        for (JudgedAnswer judgedAnswer : judgedAnswers) {
            answerEntities.add(PracticeAnswer.builder()
                    .practiceRecordId(record.getId())
                    .questionId(judgedAnswer.question().getId())
                    .userId(userId)
                    .userAnswer(judgedAnswer.userAnswer())
                    .isCorrect(Boolean.TRUE.equals(judgedAnswer.correct()) ? 1 : 0)
                    .score(judgedAnswer.score())
                    .judgeAnalysis(judgedAnswer.analysis())
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }
        practiceAnswerService.saveAnswers(answerEntities);
        wrongQuestionBookService.recordWrongAnswers(practiceSet, questions, answerEntities);
        return getPracticeResult(record.getId());
    }

    @Override
    public PracticeResultVO getPracticeResult(Long recordId) {
        PracticeRecord record = practiceRecordService.getMyRecord(recordId);
        PracticeSet practiceSet = practiceSetService.getMyPracticeSet(record.getPracticeSetId());
        List<PracticeQuestion> questions = practiceQuestionService.listByPracticeSetId(practiceSet.getId());
        List<PracticeAnswer> answers = practiceAnswerService.listByRecordId(recordId);
        Map<Long, PracticeAnswer> answerMap = answers.stream()
                .collect(LinkedHashMap::new, (map, item) -> map.put(item.getQuestionId(), item), Map::putAll);

        List<PracticeAnswerResultVO> answerResults = questions.stream()
                .map(question -> toAnswerResult(question, answerMap.get(question.getId())))
                .toList();

        return PracticeResultVO.builder()
                .recordId(record.getId())
                .practiceSetId(practiceSet.getId())
                .documentId(practiceSet.getDocumentId())
                .title(practiceSet.getTitle())
                .totalScore(record.getTotalScore())
                .objectiveScore(record.getObjectiveScore())
                .subjectiveScore(record.getSubjectiveScore())
                .status(record.getStatus())
                .submittedAt(record.getSubmittedAt())
                .answers(answerResults)
                .build();
    }

    private void ensureDocumentReady(LearningDocument document) {
        if (!STATUS_COMPLETED.equalsIgnoreCase(document.getParseStatus())) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_PARSED);
        }
        if (documentChunkService.listByDocumentId(document.getId(), document.getUserId()).isEmpty()) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_PARSED);
        }
    }

    private String resolveSetTitle(String requestTitle, String aiTitle, String documentTitle) {
        if (StringUtils.hasText(requestTitle)) {
            return requestTitle.trim();
        }
        if (StringUtils.hasText(aiTitle)) {
            return aiTitle.trim();
        }
        return documentTitle + " Practice";
    }

    private List<PracticeQuestion> buildQuestionEntities(Long practiceSetId,
                                                         List<GeneratedPracticeQuestion> generatedQuestions,
                                                         LocalDateTime now) {
        List<PracticeQuestion> entities = new ArrayList<>(generatedQuestions.size());
        for (int i = 0; i < generatedQuestions.size(); i++) {
            GeneratedPracticeQuestion generated = generatedQuestions.get(i);
            entities.add(PracticeQuestion.builder()
                    .practiceSetId(practiceSetId)
                    .questionType(generated.getQuestionType().name())
                    .stem(generated.getStem())
                    .optionsJson(toJson(generated.getOptions()))
                    .correctAnswer(generated.getCorrectAnswer())
                    .explanation(generated.getExplanation())
                    .score(safeScore(generated.getScore()))
                    .sortOrder(generated.getSortOrder() == null ? i + 1 : generated.getSortOrder())
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }
        return entities;
    }

    private PracticeSetDetailVO buildPracticeSetDetail(PracticeSet practiceSet, List<PracticeQuestion> questions) {
        return PracticeSetDetailVO.builder()
                .id(practiceSet.getId())
                .documentId(practiceSet.getDocumentId())
                .title(practiceSet.getTitle())
                .questionCount(practiceSet.getQuestionCount())
                .totalScore(practiceSet.getTotalScore())
                .createdAt(practiceSet.getCreatedAt())
                .questions(questions.stream().map(this::toQuestionVO).toList())
                .build();
    }

    private PracticeQuestionVO toQuestionVO(PracticeQuestion question) {
        return PracticeQuestionVO.builder()
                .id(question.getId())
                .questionType(question.getQuestionType())
                .stem(question.getStem())
                .options(readOptions(question.getOptionsJson()).stream().map(PracticeOptionVO::fromModel).toList())
                .score(question.getScore())
                .sortOrder(question.getSortOrder())
                .build();
    }

    private PracticeAnswerResultVO toAnswerResult(PracticeQuestion question, PracticeAnswer answer) {
        return PracticeAnswerResultVO.builder()
                .questionId(question.getId())
                .questionType(question.getQuestionType())
                .stem(question.getStem())
                .options(readOptions(question.getOptionsJson()).stream().map(PracticeOptionVO::fromModel).toList())
                .userAnswer(answer == null ? "" : answer.getUserAnswer())
                .correctAnswer(question.getCorrectAnswer())
                .correct(answer != null && Integer.valueOf(1).equals(answer.getIsCorrect()))
                .score(answer == null ? 0 : answer.getScore())
                .maxScore(question.getScore())
                .judgeAnalysis(answer == null ? "" : answer.getJudgeAnalysis())
                .explanation(question.getExplanation())
                .build();
    }

    private Map<Long, String> toAnswerMap(List<SubmitPracticeAnswerItem> answers) {
        Map<Long, String> answerMap = new LinkedHashMap<>();
        for (SubmitPracticeAnswerItem item : answers) {
            answerMap.put(item.getQuestionId(), item.getAnswer() == null ? "" : item.getAnswer());
        }
        return answerMap;
    }

    private void validateSubmittedQuestionIds(List<PracticeQuestion> questions, Map<Long, String> answerMap) {
        if (answerMap.isEmpty()) {
            throw new BusinessException(StatusCode.PRACTICE_SUBMIT_FAILED);
        }
        List<Long> questionIds = questions.stream().map(PracticeQuestion::getId).toList();
        for (Long submittedId : answerMap.keySet()) {
            if (!questionIds.contains(submittedId)) {
                throw new BusinessException(StatusCode.PRACTICE_QUESTION_NOT_FOUND);
            }
        }
    }

    private JudgedAnswer judge(PracticeQuestion question, String userAnswer) {
        QuestionType questionType = parseQuestionType(question.getQuestionType());
        return switch (questionType) {
            case SINGLE_CHOICE, TRUE_FALSE -> judgeObjective(question, userAnswer);
            case SHORT_ANSWER -> judgeShortAnswer(question, userAnswer);
        };
    }

    private JudgedAnswer judgeObjective(PracticeQuestion question, String userAnswer) {
        boolean correct = normalizeAnswer(userAnswer).equals(normalizeAnswer(question.getCorrectAnswer()));
        int score = correct ? safeScore(question.getScore()) : 0;
        String analysis = correct
                ? "Answer is correct."
                : "Answer is incorrect. Correct answer: " + question.getCorrectAnswer();
        return new JudgedAnswer(question, userAnswer, correct, score, analysis);
    }

    private JudgedAnswer judgeShortAnswer(PracticeQuestion question, String userAnswer) {
        AiJudgeResult aiJudgeResult = aiJudgeService.judgeShortAnswer(question, userAnswer);
        if (aiJudgeResult == null) {
            throw new BusinessException(StatusCode.PRACTICE_JUDGE_FAILED);
        }
        return new JudgedAnswer(
                question,
                userAnswer,
                Boolean.TRUE.equals(aiJudgeResult.getCorrect()),
                aiJudgeResult.getScore() == null ? 0 : aiJudgeResult.getScore(),
                aiJudgeResult.getAnalysis()
        );
    }

    private QuestionType parseQuestionType(String value) {
        try {
            return QuestionType.valueOf(value);
        } catch (IllegalArgumentException ex) {
            throw new BusinessException(StatusCode.PRACTICE_TYPE_UNSUPPORTED);
        }
    }

    private boolean isObjective(PracticeQuestion question) {
        QuestionType questionType = parseQuestionType(question.getQuestionType());
        return questionType == QuestionType.SINGLE_CHOICE || questionType == QuestionType.TRUE_FALSE;
    }

    private String normalizeAnswer(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private int safeScore(Integer score) {
        return score == null ? 10 : Math.max(score, 0);
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.PRACTICE_GENERATE_FAILED);
        }
    }

    private List<PracticeOption> readOptions(String optionsJson) {
        if (!StringUtils.hasText(optionsJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(optionsJson, new TypeReference<List<PracticeOption>>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.PRACTICE_GENERATE_FAILED);
        }
    }

    private record JudgedAnswer(PracticeQuestion question, String userAnswer, Boolean correct, Integer score,
                                String analysis) {
    }
}
