package com.ailearningassistant.document.vo;

import com.ailearningassistant.document.entity.LearningDocument;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningDocumentVO {

    private Long id;
    private Long userId;
    private String title;
    private String fileName;
    private String fileUrl;
    private String fileType;
    private Long fileSize;
    private String parseStatus;
    private String summaryStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static LearningDocumentVO fromEntity(LearningDocument document) {
        return LearningDocumentVO.builder()
                .id(document.getId())
                .userId(document.getUserId())
                .title(document.getTitle())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileType(document.getFileType())
                .fileSize(document.getFileSize())
                .parseStatus(document.getParseStatus())
                .summaryStatus(document.getSummaryStatus())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}
