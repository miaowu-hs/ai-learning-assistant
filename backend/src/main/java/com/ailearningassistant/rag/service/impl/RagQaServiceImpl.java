package com.ailearningassistant.rag.service.impl;

import com.ailearningassistant.ai.model.AiChatResult;
import com.ailearningassistant.ai.service.AiChatService;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.document.entity.DocumentChunk;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.service.DocumentChunkService;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.rag.dto.AskQuestionRequest;
import com.ailearningassistant.rag.dto.CreateQaSessionRequest;
import com.ailearningassistant.rag.entity.QaMessage;
import com.ailearningassistant.rag.entity.QaSession;
import com.ailearningassistant.rag.service.QaMessageService;
import com.ailearningassistant.rag.service.QaSessionService;
import com.ailearningassistant.rag.service.RagQaService;
import com.ailearningassistant.rag.service.RagRetrievalService;
import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import com.ailearningassistant.rag.vo.QaAskResponseVO;
import com.ailearningassistant.rag.vo.QaSessionHistoryVO;
import com.ailearningassistant.rag.vo.QaSessionVO;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class RagQaServiceImpl implements RagQaService {

    private static final String ROLE_USER = "USER";
    private static final String ROLE_ASSISTANT = "ASSISTANT";
    private static final String STATUS_COMPLETED = "COMPLETED";

    private final QaSessionService qaSessionService;
    private final QaMessageService qaMessageService;
    private final RagRetrievalService ragRetrievalService;
    private final AiChatService aiChatService;
    private final LearningDocumentService learningDocumentService;
    private final DocumentChunkService documentChunkService;

    @Override
    public QaSessionVO createSession(CreateQaSessionRequest request) {
        return qaSessionService.createMySession(request);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QaAskResponseVO askQuestion(Long sessionId, AskQuestionRequest request) {
        if (!StringUtils.hasText(request.getQuestion())) {
            throw new BusinessException(StatusCode.QA_QUESTION_EMPTY);
        }

        QaSession session = qaSessionService.getMySessionEntity(sessionId);
        LearningDocument document = learningDocumentService.getMyDocumentEntity(session.getDocumentId());
        ensureDocumentReady(document);

        List<ChunkReferenceVO> references = ragRetrievalService.retrieveRelevantChunks(
                document.getId(), SecurityUtils.getUserId(), request.getQuestion());
        if (references.isEmpty()) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_PARSED);
        }

        qaMessageService.saveMessage(sessionId, session.getUserId(), ROLE_USER, request.getQuestion(), List.of());
        String prompt = buildPrompt(document, request.getQuestion(), references);
        AiChatResult aiChatResult = aiChatService.generateAnswer(document, request.getQuestion(), prompt, references);
        QaMessage assistantMessage = qaMessageService.saveMessage(
                sessionId, session.getUserId(), ROLE_ASSISTANT, aiChatResult.getAnswer(), references);
        qaSessionService.touchSession(sessionId);

        return QaAskResponseVO.builder()
                .sessionId(session.getId())
                .documentId(document.getId())
                .question(request.getQuestion())
                .answer(aiChatResult.getAnswer())
                .references(references)
                .answeredAt(assistantMessage.getCreatedAt())
                .build();
    }

    @Override
    public QaSessionHistoryVO getSessionHistory(Long sessionId) {
        QaSession session = qaSessionService.getMySessionEntity(sessionId);
        return QaSessionHistoryVO.builder()
                .session(QaSessionVO.fromEntity(session))
                .messages(qaMessageService.listSessionMessages(sessionId, session.getUserId()))
                .build();
    }

    private void ensureDocumentReady(LearningDocument document) {
        if (!STATUS_COMPLETED.equalsIgnoreCase(document.getParseStatus())) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_PARSED);
        }
        List<DocumentChunk> chunks = documentChunkService.listByDocumentId(document.getId(), document.getUserId());
        if (chunks.isEmpty()) {
            throw new BusinessException(StatusCode.DOCUMENT_NOT_PARSED);
        }
    }

    private String buildPrompt(LearningDocument document, String question, List<ChunkReferenceVO> references) {
        StringBuilder builder = new StringBuilder();
        builder.append("You are answering questions based only on the provided document.\n");
        builder.append("Document title: ").append(document.getTitle()).append("\n");
        builder.append("Question: ").append(question).append("\n");
        builder.append("Retrieved references:\n");
        for (ChunkReferenceVO reference : references) {
            builder.append("- [Chunk ").append(reference.getChunkIndex()).append("] ")
                    .append(reference.getContent()).append("\n");
        }
        builder.append("Answer clearly and cite the retrieved information.");
        return builder.toString();
    }
}
