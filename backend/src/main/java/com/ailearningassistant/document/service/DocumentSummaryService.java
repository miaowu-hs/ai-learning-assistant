package com.ailearningassistant.document.service;

import com.ailearningassistant.ai.model.AiSummaryResult;
import com.ailearningassistant.document.entity.DocumentSummary;
import com.ailearningassistant.document.vo.DocumentSummaryVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface DocumentSummaryService extends IService<DocumentSummary> {

    void saveOrUpdateSummary(Long documentId, Long userId, AiSummaryResult summaryResult);

    DocumentSummaryVO getMyDocumentSummary(Long documentId, Long userId);
}
