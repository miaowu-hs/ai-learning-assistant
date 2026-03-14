package com.ailearningassistant.document.service.impl;

import com.ailearningassistant.ai.model.AiSummaryResult;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.document.entity.DocumentSummary;
import com.ailearningassistant.document.mapper.DocumentSummaryMapper;
import com.ailearningassistant.document.service.DocumentSummaryService;
import com.ailearningassistant.document.vo.DocumentSummaryVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentSummaryServiceImpl extends ServiceImpl<DocumentSummaryMapper, DocumentSummary>
        implements DocumentSummaryService {

    private final ObjectMapper objectMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrUpdateSummary(Long documentId, Long userId, AiSummaryResult summaryResult) {
        LocalDateTime now = LocalDateTime.now();
        DocumentSummary existing = lambdaQuery()
                .eq(DocumentSummary::getDocumentId, documentId)
                .one();

        String outlineJson = toJson(summaryResult.getOutline());
        String keyPointsJson = toJson(summaryResult.getKeyPoints());

        if (existing == null) {
            DocumentSummary summary = DocumentSummary.builder()
                    .documentId(documentId)
                    .userId(userId)
                    .shortSummary(summaryResult.getShortSummary())
                    .outline(outlineJson)
                    .keyPoints(keyPointsJson)
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            save(summary);
            return;
        }

        lambdaUpdate()
                .eq(DocumentSummary::getDocumentId, documentId)
                .set(DocumentSummary::getShortSummary, summaryResult.getShortSummary())
                .set(DocumentSummary::getOutline, outlineJson)
                .set(DocumentSummary::getKeyPoints, keyPointsJson)
                .set(DocumentSummary::getUpdatedAt, now)
                .update();
    }

    @Override
    public DocumentSummaryVO getMyDocumentSummary(Long documentId, Long userId) {
        DocumentSummary summary = lambdaQuery()
                .eq(DocumentSummary::getDocumentId, documentId)
                .eq(DocumentSummary::getUserId, userId)
                .one();
        if (summary == null) {
            throw new BusinessException(StatusCode.DOCUMENT_SUMMARY_NOT_FOUND);
        }
        return DocumentSummaryVO.builder()
                .documentId(summary.getDocumentId())
                .shortSummary(summary.getShortSummary())
                .outline(fromJson(summary.getOutline()))
                .keyPoints(fromJson(summary.getKeyPoints()))
                .createdAt(summary.getCreatedAt())
                .updatedAt(summary.getUpdatedAt())
                .build();
    }

    private String toJson(List<String> value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.DOCUMENT_SUMMARY_FAILED);
        }
    }

    private List<String> fromJson(String value) {
        try {
            return objectMapper.readValue(value, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.DOCUMENT_SUMMARY_FAILED);
        }
    }
}
