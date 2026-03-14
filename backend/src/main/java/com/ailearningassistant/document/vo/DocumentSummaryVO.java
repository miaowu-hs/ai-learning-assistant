package com.ailearningassistant.document.vo;

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
public class DocumentSummaryVO {

    private Long documentId;
    private String shortSummary;
    private List<String> outline;
    private List<String> keyPoints;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
