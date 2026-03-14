package com.ailearningassistant.document.service;

import com.ailearningassistant.document.vo.DocumentParseResultVO;
import com.ailearningassistant.document.vo.DocumentSummaryVO;

public interface DocumentParseService {

    DocumentParseResultVO parseMyDocument(Long documentId);

    DocumentSummaryVO getMyDocumentSummary(Long documentId);
}
