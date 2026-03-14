package com.ailearningassistant.document.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentParseResultVO {

    private Long documentId;
    private Integer chunkCount;
    private String parseStatus;
    private String summaryStatus;
}
