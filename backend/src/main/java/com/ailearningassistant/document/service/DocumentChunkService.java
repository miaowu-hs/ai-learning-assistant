package com.ailearningassistant.document.service;

import com.ailearningassistant.document.entity.DocumentChunk;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface DocumentChunkService extends IService<DocumentChunk> {

    void replaceDocumentChunks(Long documentId, Long userId, List<String> chunks);

    List<DocumentChunk> listByDocumentId(Long documentId, Long userId);
}
