package com.ailearningassistant.document.service.impl;

import com.ailearningassistant.document.entity.DocumentChunk;
import com.ailearningassistant.document.mapper.DocumentChunkMapper;
import com.ailearningassistant.document.service.DocumentChunkService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentChunkServiceImpl extends ServiceImpl<DocumentChunkMapper, DocumentChunk>
        implements DocumentChunkService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceDocumentChunks(Long documentId, Long userId, List<String> chunks) {
        remove(new LambdaQueryWrapper<DocumentChunk>().eq(DocumentChunk::getDocumentId, documentId));

        LocalDateTime now = LocalDateTime.now();
        List<DocumentChunk> entities = new ArrayList<>(chunks.size());
        for (int index = 0; index < chunks.size(); index++) {
            String chunk = chunks.get(index);
            entities.add(DocumentChunk.builder()
                    .documentId(documentId)
                    .userId(userId)
                    .chunkIndex(index + 1)
                    .chunkContent(chunk)
                    .chunkLength(chunk.length())
                    .createdAt(now)
                    .updatedAt(now)
                    .build());
        }

        if (!entities.isEmpty()) {
            saveBatch(entities);
        }
    }

    @Override
    public List<DocumentChunk> listByDocumentId(Long documentId, Long userId) {
        return lambdaQuery()
                .eq(DocumentChunk::getDocumentId, documentId)
                .eq(DocumentChunk::getUserId, userId)
                .orderByAsc(DocumentChunk::getChunkIndex)
                .list();
    }
}
