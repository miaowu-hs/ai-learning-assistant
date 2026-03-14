package com.ailearningassistant.rag.service.impl;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.rag.entity.QaMessage;
import com.ailearningassistant.rag.mapper.QaMessageMapper;
import com.ailearningassistant.rag.service.QaMessageService;
import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import com.ailearningassistant.rag.vo.QaMessageVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QaMessageServiceImpl extends ServiceImpl<QaMessageMapper, QaMessage> implements QaMessageService {

    private final ObjectMapper objectMapper;

    @Override
    public QaMessage saveMessage(Long sessionId,
                                 Long userId,
                                 String role,
                                 String content,
                                 List<ChunkReferenceVO> references) {
        LocalDateTime now = LocalDateTime.now();
        QaMessage message = QaMessage.builder()
                .sessionId(sessionId)
                .userId(userId)
                .role(role)
                .content(content)
                .referencesJson(toJson(references))
                .createdAt(now)
                .updatedAt(now)
                .build();
        boolean saved = save(message);
        if (!saved) {
            throw new BusinessException(StatusCode.QA_MESSAGE_SAVE_FAILED);
        }
        return message;
    }

    @Override
    public List<QaMessageVO> listSessionMessages(Long sessionId, Long userId) {
        return lambdaQuery()
                .eq(QaMessage::getSessionId, sessionId)
                .eq(QaMessage::getUserId, userId)
                .orderByAsc(QaMessage::getCreatedAt)
                .list()
                .stream()
                .map(message -> QaMessageVO.builder()
                        .id(message.getId())
                        .role(message.getRole())
                        .content(message.getContent())
                        .references(fromJson(message.getReferencesJson()))
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();
    }

    private String toJson(List<ChunkReferenceVO> references) {
        try {
            return objectMapper.writeValueAsString(references == null ? Collections.emptyList() : references);
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.QA_MESSAGE_SAVE_FAILED);
        }
    }

    private List<ChunkReferenceVO> fromJson(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(value, new TypeReference<List<ChunkReferenceVO>>() {
            });
        } catch (JsonProcessingException ex) {
            throw new BusinessException(StatusCode.QA_GENERATE_FAILED);
        }
    }
}
