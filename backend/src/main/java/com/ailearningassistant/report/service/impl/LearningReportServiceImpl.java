package com.ailearningassistant.report.service.impl;

import com.ailearningassistant.ai.service.AiSuggestionService;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.practice.entity.PracticeAnswer;
import com.ailearningassistant.practice.entity.PracticeRecord;
import com.ailearningassistant.practice.service.PracticeAnswerService;
import com.ailearningassistant.practice.service.PracticeRecordService;
import com.ailearningassistant.rag.entity.QaMessage;
import com.ailearningassistant.rag.service.QaMessageService;
import com.ailearningassistant.report.service.LearningReportService;
import com.ailearningassistant.report.vo.KnowledgePointStatVO;
import com.ailearningassistant.report.vo.LearningReportVO;
import com.ailearningassistant.wrongbook.entity.WrongQuestionBook;
import com.ailearningassistant.wrongbook.service.WrongQuestionBookService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class LearningReportServiceImpl implements LearningReportService {

    private static final long REPORT_SUGGESTION_TIMEOUT_SECONDS = 3L;

    private final LearningDocumentService learningDocumentService;
    private final QaMessageService qaMessageService;
    private final PracticeRecordService practiceRecordService;
    private final PracticeAnswerService practiceAnswerService;
    private final WrongQuestionBookService wrongQuestionBookService;
    private final AiSuggestionService aiSuggestionService;

    @Override
    public LearningReportVO getMyLearningReport() {
        try {
            Long userId = SecurityUtils.getUserId();
            long documentCount = learningDocumentService.count(
                    new LambdaQueryWrapper<LearningDocument>().eq(LearningDocument::getUserId, userId));
            long qaCount = qaMessageService.count(new LambdaQueryWrapper<QaMessage>()
                    .eq(QaMessage::getUserId, userId)
                    .eq(QaMessage::getRole, "USER"));
            long practiceCount = practiceRecordService.count(
                    new LambdaQueryWrapper<PracticeRecord>().eq(PracticeRecord::getUserId, userId));
            long totalAnswerCount = practiceAnswerService.count(
                    new LambdaQueryWrapper<PracticeAnswer>().eq(PracticeAnswer::getUserId, userId));
            long correctAnswerCount = practiceAnswerService.count(new LambdaQueryWrapper<PracticeAnswer>()
                    .eq(PracticeAnswer::getUserId, userId)
                    .eq(PracticeAnswer::getIsCorrect, 1));

            BigDecimal accuracy = totalAnswerCount == 0
                    ? BigDecimal.ZERO
                    : BigDecimal.valueOf(correctAnswerCount)
                    .divide(BigDecimal.valueOf(totalAnswerCount), 4, RoundingMode.HALF_UP);

            List<KnowledgePointStatVO> knowledgePointStats = buildKnowledgePointStats(userId);
            List<String> suggestions = generateSuggestionsSafely(
                    documentCount,
                    qaCount,
                    practiceCount,
                    accuracy,
                    knowledgePointStats
            );

            return LearningReportVO.builder()
                    .documentCount(documentCount)
                    .qaCount(qaCount)
                    .practiceCount(practiceCount)
                    .accuracy(accuracy)
                    .recentWrongKnowledgePoints(knowledgePointStats)
                    .suggestions(suggestions)
                    .build();
        } catch (BusinessException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new BusinessException(StatusCode.REPORT_GENERATE_FAILED);
        }
    }

    private List<KnowledgePointStatVO> buildKnowledgePointStats(Long userId) {
        List<WrongQuestionBook> recentWrongQuestions = wrongQuestionBookService.listRecentWrongQuestions(userId, 20);
        Map<String, Integer> grouped = recentWrongQuestions.stream()
                .collect(Collectors.groupingBy(
                        item -> item.getKnowledgePoint() == null || item.getKnowledgePoint().isBlank()
                                ? "General"
                                : item.getKnowledgePoint(),
                        Collectors.summingInt(item -> item.getWrongCount() == null ? 1 : item.getWrongCount())
                ));

        return grouped.entrySet().stream()
                .sorted((left, right) -> right.getValue().compareTo(left.getValue()))
                .limit(5)
                .map(entry -> KnowledgePointStatVO.builder()
                        .knowledgePoint(entry.getKey())
                        .count(entry.getValue())
                        .build())
                .toList();
    }

    private List<String> generateSuggestionsSafely(long documentCount,
                                                   long qaCount,
                                                   long practiceCount,
                                                   BigDecimal accuracy,
                                                   List<KnowledgePointStatVO> knowledgePointStats) {
        List<String> fallbackSuggestions =
                buildFallbackSuggestions(documentCount, qaCount, practiceCount, accuracy, knowledgePointStats);

        try {
            return CompletableFuture.supplyAsync(() -> aiSuggestionService.generateSuggestions(
                            (int) documentCount,
                            (int) qaCount,
                            (int) practiceCount,
                            accuracy,
                            knowledgePointStats
                    ))
                    .completeOnTimeout(
                            fallbackSuggestions,
                            REPORT_SUGGESTION_TIMEOUT_SECONDS,
                            TimeUnit.SECONDS
                    )
                    .exceptionally(ex -> {
                        log.warn("AI suggestion generation failed, fallback suggestions will be used", ex);
                        return fallbackSuggestions;
                    })
                    .join();
        } catch (Exception ex) {
            log.warn("AI suggestion generation failed, fallback suggestions will be used", ex);
            return fallbackSuggestions;
        }
    }

    private List<String> buildFallbackSuggestions(long documentCount,
                                                  long qaCount,
                                                  long practiceCount,
                                                  BigDecimal accuracy,
                                                  List<KnowledgePointStatVO> knowledgePointStats) {
        List<String> suggestions = new ArrayList<>();

        if (documentCount == 0) {
            suggestions.add("先上传并解析至少一份学习文档，再开始问答和练习，这样学习报告会逐步形成。");
        }

        if (qaCount == 0) {
            suggestions.add("你还没有进行文档问答，建议围绕已上传文档提出 3 到 5 个问题，帮助快速理解重点内容。");
        } else if (qaCount < documentCount) {
            suggestions.add("当前问答次数偏少，建议针对每份文档至少进行一轮提问，补齐理解盲区。");
        }

        if (practiceCount == 0) {
            suggestions.add("你还没有完成练习，建议从当前文档生成一套练习题，检验对核心知识点的掌握情况。");
        }

        if (accuracy.compareTo(new BigDecimal("0.6")) < 0) {
            suggestions.add("当前正确率偏低，建议优先复盘错题并重新练习，先提升基础题目的稳定性。");
        } else if (accuracy.compareTo(new BigDecimal("0.85")) < 0) {
            suggestions.add("当前正确率处于提升阶段，建议结合错题本做针对性复练，并补充关键知识点问答。");
        } else if (practiceCount > 0) {
            suggestions.add("当前正确率表现较好，建议继续保持练习节奏，并通过文档问答加深对细节的理解。");
        }

        if (!knowledgePointStats.isEmpty()) {
            String focusKnowledgePoints = knowledgePointStats.stream()
                    .limit(2)
                    .map(KnowledgePointStatVO::getKnowledgePoint)
                    .collect(Collectors.joining("、"));
            suggestions.add("近期错题主要集中在「" + focusKnowledgePoints + "」，建议优先回看对应文档片段并再次练习。");
        }

        if (suggestions.isEmpty()) {
            suggestions.add("继续保持当前学习节奏，适当增加问答和练习次数，系统会基于后续数据生成更准确的学习建议。");
        }

        return suggestions.stream().limit(3).toList();
    }
}
