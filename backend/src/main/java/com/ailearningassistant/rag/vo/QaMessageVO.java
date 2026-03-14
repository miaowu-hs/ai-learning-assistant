package com.ailearningassistant.rag.vo;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaMessageVO {

    private Long id;
    private String role;
    private String content;
    private List<ChunkReferenceVO> references;
    private LocalDateTime createdAt;
}
