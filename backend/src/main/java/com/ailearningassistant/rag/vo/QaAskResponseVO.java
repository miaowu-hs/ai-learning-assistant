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
public class QaAskResponseVO {

    private Long sessionId;
    private Long documentId;
    private String question;
    private String answer;
    private List<ChunkReferenceVO> references;
    private LocalDateTime answeredAt;
}
