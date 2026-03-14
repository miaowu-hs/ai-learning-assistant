package com.ailearningassistant.rag.vo;

import com.ailearningassistant.document.entity.DocumentChunk;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChunkReferenceVO {

    private Long chunkId;
    private Integer chunkIndex;
    private String content;
    private Integer score;

    public static ChunkReferenceVO fromEntity(DocumentChunk chunk, Integer score) {
        return ChunkReferenceVO.builder()
                .chunkId(chunk.getId())
                .chunkIndex(chunk.getChunkIndex())
                .content(chunk.getChunkContent())
                .score(score)
                .build();
    }
}
