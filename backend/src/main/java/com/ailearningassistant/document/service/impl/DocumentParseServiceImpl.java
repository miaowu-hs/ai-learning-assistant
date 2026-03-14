package com.ailearningassistant.document.service.impl;

import com.ailearningassistant.ai.model.AiSummaryResult;
import com.ailearningassistant.ai.service.AiSummaryService;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.document.config.DocumentStorageProperties;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.parser.DocumentParser;
import com.ailearningassistant.document.parser.DocumentParserRegistry;
import com.ailearningassistant.document.service.DocumentChunkService;
import com.ailearningassistant.document.service.DocumentParseService;
import com.ailearningassistant.document.service.DocumentSummaryService;
import com.ailearningassistant.document.service.DocumentTextProcessingService;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.document.vo.DocumentParseResultVO;
import com.ailearningassistant.document.vo.DocumentSummaryVO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentParseServiceImpl implements DocumentParseService {

    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_PROCESSING = "PROCESSING";
    private static final String STATUS_COMPLETED = "COMPLETED";
    private static final String STATUS_FAILED = "FAILED";
    private static final String UPLOAD_PREFIX = "/uploads/";

    private final LearningDocumentService learningDocumentService;
    private final DocumentParserRegistry documentParserRegistry;
    private final DocumentTextProcessingService documentTextProcessingService;
    private final DocumentChunkService documentChunkService;
    private final DocumentSummaryService documentSummaryService;
    private final AiSummaryService aiSummaryService;
    private final DocumentStorageProperties documentStorageProperties;

    @Override
    public DocumentParseResultVO parseMyDocument(Long documentId) {
        LearningDocument document = learningDocumentService.getMyDocumentEntity(documentId);
        learningDocumentService.updateDocumentStatuses(documentId, STATUS_PROCESSING, STATUS_PROCESSING);

        boolean parseCompleted = false;
        try {
            Path filePath = resolveDocumentPath(document.getFileUrl());
            if (!Files.exists(filePath)) {
                throw new BusinessException(StatusCode.DOCUMENT_PARSE_FAILED.getCode(), "Source file does not exist");
            }

            DocumentParser parser = documentParserRegistry.getParser(document.getFileType());
            String rawText = parser.parse(filePath);
            String cleanedText = documentTextProcessingService.cleanText(rawText);
            List<String> chunks = documentTextProcessingService.splitIntoChunks(cleanedText);
            documentChunkService.replaceDocumentChunks(documentId, document.getUserId(), chunks);

            learningDocumentService.updateDocumentStatuses(documentId, STATUS_COMPLETED, STATUS_PROCESSING);
            parseCompleted = true;

            AiSummaryResult summaryResult = aiSummaryService.generateSummary(document, chunks);
            documentSummaryService.saveOrUpdateSummary(documentId, document.getUserId(), summaryResult);
            learningDocumentService.updateDocumentStatuses(documentId, STATUS_COMPLETED, STATUS_COMPLETED);

            return DocumentParseResultVO.builder()
                    .documentId(documentId)
                    .chunkCount(chunks.size())
                    .parseStatus(STATUS_COMPLETED)
                    .summaryStatus(STATUS_COMPLETED)
                    .build();
        } catch (BusinessException ex) {
            handleFailure(documentId, parseCompleted);
            throw ex;
        } catch (IOException ex) {
            handleFailure(documentId, parseCompleted);
            throw new BusinessException(parseCompleted ? StatusCode.DOCUMENT_SUMMARY_FAILED : StatusCode.DOCUMENT_PARSE_FAILED);
        } catch (Exception ex) {
            handleFailure(documentId, parseCompleted);
            throw new BusinessException(parseCompleted ? StatusCode.DOCUMENT_SUMMARY_FAILED : StatusCode.DOCUMENT_PARSE_FAILED);
        }
    }

    @Override
    public DocumentSummaryVO getMyDocumentSummary(Long documentId) {
        LearningDocument document = learningDocumentService.getMyDocumentEntity(documentId);
        return documentSummaryService.getMyDocumentSummary(document.getId(), SecurityUtils.getUserId());
    }

    private Path resolveDocumentPath(String fileUrl) {
        if (fileUrl == null || !fileUrl.startsWith(UPLOAD_PREFIX)) {
            throw new BusinessException(StatusCode.DOCUMENT_PARSE_FAILED.getCode(), "Invalid file url");
        }
        String relativePath = fileUrl.substring(UPLOAD_PREFIX.length());
        return Path.of(documentStorageProperties.getUploadDir())
                .toAbsolutePath()
                .normalize()
                .resolve(relativePath)
                .normalize();
    }

    private void handleFailure(Long documentId, boolean parseCompleted) {
        if (parseCompleted) {
            learningDocumentService.updateDocumentStatuses(documentId, STATUS_COMPLETED, STATUS_FAILED);
            return;
        }
        learningDocumentService.updateDocumentStatuses(documentId, STATUS_FAILED, STATUS_FAILED);
    }
}
