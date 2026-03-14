package com.ailearningassistant.rag.service.impl;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.rag.dto.CreateQaSessionRequest;
import com.ailearningassistant.rag.entity.QaSession;
import com.ailearningassistant.rag.mapper.QaSessionMapper;
import com.ailearningassistant.rag.service.QaSessionService;
import com.ailearningassistant.rag.vo.QaSessionVO;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class QaSessionServiceImpl extends ServiceImpl<QaSessionMapper, QaSession> implements QaSessionService {

    private final LearningDocumentService learningDocumentService;

    @Override
    public QaSessionVO createMySession(CreateQaSessionRequest request) {
        LearningDocument document = learningDocumentService.getMyDocumentEntity(request.getDocumentId());
        Long userId = SecurityUtils.getUserId();
        LocalDateTime now = LocalDateTime.now();
        QaSession session = QaSession.builder()
                .userId(userId)
                .documentId(document.getId())
                .title(resolveTitle(request.getTitle(), document.getTitle()))
                .createdAt(now)
                .updatedAt(now)
                .build();
        boolean saved = save(session);
        if (!saved) {
            throw new BusinessException(StatusCode.SYSTEM_ERROR);
        }
        return QaSessionVO.fromEntity(session);
    }

    @Override
    public QaSession getMySessionEntity(Long sessionId) {
        QaSession session = lambdaQuery()
                .eq(QaSession::getId, sessionId)
                .eq(QaSession::getUserId, SecurityUtils.getUserId())
                .one();
        if (session == null) {
            throw new BusinessException(StatusCode.QA_SESSION_NOT_FOUND);
        }
        return session;
    }

    @Override
    public void touchSession(Long sessionId) {
        boolean updated = lambdaUpdate()
                .eq(QaSession::getId, sessionId)
                .set(QaSession::getUpdatedAt, LocalDateTime.now())
                .update();
        if (!updated) {
            throw new BusinessException(StatusCode.QA_SESSION_NOT_FOUND);
        }
    }

    private String resolveTitle(String title, String documentTitle) {
        if (StringUtils.hasText(title)) {
            return title.trim();
        }
        return documentTitle + " QA";
    }
}
