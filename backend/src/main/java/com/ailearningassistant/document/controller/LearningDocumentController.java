package com.ailearningassistant.document.controller;

import com.ailearningassistant.common.api.PageResult;
import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.document.dto.DocumentPageQueryRequest;
import com.ailearningassistant.document.dto.DocumentUploadRequest;
import com.ailearningassistant.document.service.DocumentParseService;
import com.ailearningassistant.document.service.LearningDocumentService;
import com.ailearningassistant.document.vo.DocumentParseResultVO;
import com.ailearningassistant.document.vo.DocumentSummaryVO;
import com.ailearningassistant.document.vo.LearningDocumentVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class LearningDocumentController {

    private final LearningDocumentService learningDocumentService;
    private final DocumentParseService documentParseService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Result<LearningDocumentVO> upload(@Valid @ModelAttribute DocumentUploadRequest request) {
        return Result.success(learningDocumentService.uploadDocument(request));
    }

    @GetMapping
    public Result<PageResult<LearningDocumentVO>> pageMyDocuments(@Valid DocumentPageQueryRequest request) {
        return Result.success(learningDocumentService.pageMyDocuments(request));
    }

    @GetMapping("/{id}")
    public Result<LearningDocumentVO> getDetail(@PathVariable Long id) {
        return Result.success(learningDocumentService.getMyDocumentDetail(id));
    }

    @PostMapping("/{id}/parse")
    public Result<DocumentParseResultVO> parse(@PathVariable Long id) {
        return Result.success(documentParseService.parseMyDocument(id));
    }

    @GetMapping("/{id}/summary")
    public Result<DocumentSummaryVO> getSummary(@PathVariable Long id) {
        return Result.success(documentParseService.getMyDocumentSummary(id));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        learningDocumentService.deleteMyDocument(id);
        return Result.success();
    }
}
