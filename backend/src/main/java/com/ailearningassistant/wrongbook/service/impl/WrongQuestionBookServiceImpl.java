package com.ailearningassistant.wrongbook.service.impl;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.practice.entity.PracticeAnswer;
import com.ailearningassistant.practice.entity.PracticeQuestion;
import com.ailearningassistant.practice.entity.PracticeSet;
import com.ailearningassistant.practice.model.PracticeOption;
import com.ailearningassistant.practice.service.PracticeQuestionService;
import com.ailearningassistant.practice.service.PracticeSetService;
import com.ailearningassistant.practice.vo.PracticeOptionVO;
import com.ailearningassistant.practice.vo.PracticeQuestionVO;
import com.ailearningassistant.practice.vo.PracticeSetDetailVO;
import com.ailearningassistant.wrongbook.dto.RegenerateWrongPracticeRequest;
import com.ailearningassistant.wrongbook.entity.WrongQuestionBook;
import com.ailearningassistant.wrongbook.mapper.WrongQuestionBookMapper;
import com.ailearningassistant.wrongbook.service.WrongQuestionBookService;
import com.ailearningassistant.wrongbook.vo.WrongQuestionVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WrongQuestionBookServiceImpl extends ServiceImpl<WrongQuestionBookMapper, WrongQuestionBook>
        implements WrongQuestionBookService {

    private final PracticeSetService practiceSetService;
    private final PracticeQuestionService practiceQuestionService;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void recordWrongAnswers(PracticeSet practiceSet, List<PracticeQuestion> questions, List<PracticeAnswer> answers) {
        Long userId = SecurityUtils.getUserId();
        Map<Long, PracticeQuestion> questionMap = questions.stream()
                .collect(LinkedHashMap::new, (map, question) -> map.put(question.getId(), question), Map::putAll);
        LocalDateTime now = LocalDateTime.now();

        for (PracticeAnswer answer : answers) {
            if (Integer.valueOf(1).equals(answer.getIsCorrect())) {
                continue;
            }

            PracticeQuestion question = questionMap.get(answer.getQuestionId());
            if (question == null) {
                continue;
            }

            WrongQuestionBook existing = lambdaQuery()
                    .eq(WrongQuestionBook::getUserId, userId)
                    .eq(WrongQuestionBook::getQuestionId, question.getId())
                    .one();

            if (existing == null) {
                WrongQuestionBook entity = WrongQuestionBook.builder()
                        .userId(userId)
                        .documentId(practiceSet.getDocumentId())
                        .practiceSetId(practiceSet.getId())
                        .questionId(question.getId())
                        .questionType(question.getQuestionType())
                        .stem(question.getStem())
                        .optionsJson(question.getOptionsJson())
                        .correctAnswer(question.getCorrectAnswer())
                        .explanation(question.getExplanation())
                        .knowledgePoint(extractKnowledgePoint(question))
                        .wrongCount(1)
                        .lastUserAnswer(answer.getUserAnswer())
                        .createdAt(now)
                        .updatedAt(now)
                        .build();
                save(entity);
                continue;
            }

            lambdaUpdate()
                    .eq(WrongQuestionBook::getId, existing.getId())
                    .set(WrongQuestionBook::getPracticeSetId, practiceSet.getId())
                    .set(WrongQuestionBook::getDocumentId, practiceSet.getDocumentId())
                    .set(WrongQuestionBook::getQuestionType, question.getQuestionType())
                    .set(WrongQuestionBook::getStem, question.getStem())
                    .set(WrongQuestionBook::getOptionsJson, question.getOptionsJson())
                    .set(WrongQuestionBook::getCorrectAnswer, question.getCorrectAnswer())
                    .set(WrongQuestionBook::getExplanation, question.getExplanation())
                    .set(WrongQuestionBook::getKnowledgePoint, extractKnowledgePoint(question))
                    .set(WrongQuestionBook::getWrongCount, existing.getWrongCount() == null ? 1 : existing.getWrongCount() + 1)
                    .set(WrongQuestionBook::getLastUserAnswer, answer.getUserAnswer())
                    .set(WrongQuestionBook::getUpdatedAt, now)
                    .update();
        }
    }

    @Override
    public List<WrongQuestionVO> listMyWrongQuestions() {
        Long userId = SecurityUtils.getUserId();
        return lambdaQuery()
                .eq(WrongQuestionBook::getUserId, userId)
                .orderByDesc(WrongQuestionBook::getUpdatedAt)
                .list()
                .stream()
                .map(this::toWrongQuestionVO)
                .toList();
    }

    @Override
    public void deleteMyWrongQuestion(Long id) {
        boolean removed = remove(new LambdaQueryWrapper<WrongQuestionBook>()
                .eq(WrongQuestionBook::getId, id)
                .eq(WrongQuestionBook::getUserId, SecurityUtils.getUserId()));
        if (!removed) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_NOT_FOUND);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PracticeSetDetailVO regeneratePractice(RegenerateWrongPracticeRequest request) {
        List<WrongQuestionBook> wrongQuestions = resolveWrongQuestionsForRegeneration(request);
        if (wrongQuestions.isEmpty()) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_NOT_FOUND);
        }

        Long documentId = wrongQuestions.get(0).getDocumentId();
        if (wrongQuestions.stream().map(WrongQuestionBook::getDocumentId).filter(Objects::nonNull).distinct().count() > 1) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_REGENERATE_FAILED.getCode(),
                    "Selected wrong questions must belong to the same document");
        }

        LocalDateTime now = LocalDateTime.now();
        PracticeSet practiceSet = PracticeSet.builder()
                .userId(SecurityUtils.getUserId())
                .documentId(documentId)
                .title(resolvePracticeTitle(request.getTitle()))
                .questionCount(wrongQuestions.size())
                .totalScore(wrongQuestions.stream().mapToInt(item -> resolveScoreFromWrongQuestion(item)).sum())
                .generationPayload(toJson(wrongQuestions.stream().map(WrongQuestionBook::getId).toList()))
                .createdAt(now)
                .updatedAt(now)
                .build();

        if (!practiceSetService.save(practiceSet)) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_REGENERATE_FAILED);
        }

        List<PracticeQuestion> regeneratedQuestions = wrongQuestions.stream()
                .map(item -> PracticeQuestion.builder()
                        .practiceSetId(practiceSet.getId())
                        .questionType(item.getQuestionType())
                        .stem(item.getStem())
                        .optionsJson(item.getOptionsJson())
                        .correctAnswer(item.getCorrectAnswer())
                        .explanation(item.getExplanation())
                        .score(resolveScoreFromWrongQuestion(item))
                        .sortOrder(0)
                        .createdAt(now)
                        .updatedAt(now)
                        .build())
                .toList();

        for (int i = 0; i < regeneratedQuestions.size(); i++) {
            regeneratedQuestions.get(i).setSortOrder(i + 1);
        }
        practiceQuestionService.replaceQuestions(practiceSet.getId(), regeneratedQuestions);
        List<PracticeQuestion> savedQuestions = practiceQuestionService.listByPracticeSetId(practiceSet.getId());

        return PracticeSetDetailVO.builder()
                .id(practiceSet.getId())
                .documentId(practiceSet.getDocumentId())
                .title(practiceSet.getTitle())
                .questionCount(practiceSet.getQuestionCount())
                .totalScore(practiceSet.getTotalScore())
                .createdAt(practiceSet.getCreatedAt())
                .questions(savedQuestions.stream().map(this::toPracticeQuestionVO).toList())
                .build();
    }

    @Override
    public List<WrongQuestionBook> listRecentWrongQuestions(Long userId, int limit) {
        return lambdaQuery()
                .eq(WrongQuestionBook::getUserId, userId)
                .orderByDesc(WrongQuestionBook::getUpdatedAt)
                .last("limit " + Math.max(1, limit))
                .list();
    }

    private List<WrongQuestionBook> resolveWrongQuestionsForRegeneration(RegenerateWrongPracticeRequest request) {
        Long userId = SecurityUtils.getUserId();
        if (request.getWrongQuestionIds() != null && !request.getWrongQuestionIds().isEmpty()) {
            List<WrongQuestionBook> wrongQuestions = lambdaQuery()
                    .eq(WrongQuestionBook::getUserId, userId)
                    .in(WrongQuestionBook::getId, request.getWrongQuestionIds())
                    .orderByDesc(WrongQuestionBook::getUpdatedAt)
                    .list();
            if (wrongQuestions.size() != request.getWrongQuestionIds().size()) {
                throw new BusinessException(StatusCode.WRONG_QUESTION_NOT_FOUND);
            }
            return wrongQuestions;
        }
        if (request.getDocumentId() == null) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_REGENERATE_FAILED.getCode(),
                    "documentId or wrongQuestionIds is required");
        }
        return lambdaQuery()
                .eq(WrongQuestionBook::getUserId, userId)
                .eq(WrongQuestionBook::getDocumentId, request.getDocumentId())
                .orderByDesc(WrongQuestionBook::getUpdatedAt)
                .list();
    }

    private WrongQuestionVO toWrongQuestionVO(WrongQuestionBook wrongQuestion) {
        return WrongQuestionVO.builder()
                .id(wrongQuestion.getId())
                .documentId(wrongQuestion.getDocumentId())
                .practiceSetId(wrongQuestion.getPracticeSetId())
                .questionId(wrongQuestion.getQuestionId())
                .questionType(wrongQuestion.getQuestionType())
                .stem(wrongQuestion.getStem())
                .options(readOptions(wrongQuestion.getOptionsJson()).stream().map(PracticeOptionVO::fromModel).toList())
                .correctAnswer(wrongQuestion.getCorrectAnswer())
                .explanation(wrongQuestion.getExplanation())
                .knowledgePoint(wrongQuestion.getKnowledgePoint())
                .wrongCount(wrongQuestion.getWrongCount())
                .lastUserAnswer(wrongQuestion.getLastUserAnswer())
                .updatedAt(wrongQuestion.getUpdatedAt())
                .build();
    }

    private PracticeQuestionVO toPracticeQuestionVO(PracticeQuestion question) {
        return PracticeQuestionVO.builder()
                .id(question.getId())
                .questionType(question.getQuestionType())
                .stem(question.getStem())
                .options(readOptions(question.getOptionsJson()).stream().map(PracticeOptionVO::fromModel).toList())
                .score(question.getScore())
                .sortOrder(question.getSortOrder())
                .build();
    }

    private List<PracticeOption> readOptions(String optionsJson) {
        if (!StringUtils.hasText(optionsJson)) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(optionsJson, new TypeReference<List<PracticeOption>>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_REGENERATE_FAILED);
        }
    }

    private String extractKnowledgePoint(PracticeQuestion question) {
        String content = StringUtils.hasText(question.getExplanation()) ? question.getExplanation() : question.getStem();
        if (!StringUtils.hasText(content)) {
            return "General";
        }
        String normalized = content.replaceAll("\\s+", " ").trim();
        return normalized.length() > 60 ? normalized.substring(0, 60) : normalized;
    }

    private String resolvePracticeTitle(String title) {
        if (StringUtils.hasText(title)) {
            return title.trim();
        }
        return "Wrong Book Review Practice";
    }

    private Integer resolveScoreFromWrongQuestion(WrongQuestionBook wrongQuestion) {
        PracticeQuestion question = practiceQuestionService.getById(wrongQuestion.getQuestionId());
        if (question != null && question.getScore() != null) {
            return question.getScore();
        }
        return "SHORT_ANSWER".equals(wrongQuestion.getQuestionType()) ? 20 : 10;
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.WRONG_QUESTION_REGENERATE_FAILED);
        }
    }
}
