package com.ailearningassistant.document.service;

import com.ailearningassistant.common.api.PageResult;
import com.ailearningassistant.document.dto.DocumentPageQueryRequest;
import com.ailearningassistant.document.dto.DocumentUploadRequest;
import com.ailearningassistant.document.entity.LearningDocument;
import com.ailearningassistant.document.vo.LearningDocumentVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LearningDocumentService extends IService<LearningDocument> {

    LearningDocumentVO uploadDocument(DocumentUploadRequest request);

    PageResult<LearningDocumentVO> pageMyDocuments(DocumentPageQueryRequest request);

    LearningDocumentVO getMyDocumentDetail(Long id);

    void deleteMyDocument(Long id);

    LearningDocument getMyDocumentEntity(Long id);

    void updateDocumentStatuses(Long id, String parseStatus, String summaryStatus);
}
