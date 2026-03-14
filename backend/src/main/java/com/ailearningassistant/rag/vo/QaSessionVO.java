package com.ailearningassistant.rag.vo;

import com.ailearningassistant.rag.entity.QaSession;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QaSessionVO {

    private Long id;
    private Long documentId;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static QaSessionVO fromEntity(QaSession session) {
        return QaSessionVO.builder()
                .id(session.getId())
                .documentId(session.getDocumentId())
                .title(session.getTitle())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
}
