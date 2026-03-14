package com.ailearningassistant.report.vo;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningReportVO {

    private Long documentCount;
    private Long qaCount;
    private Long practiceCount;
    private BigDecimal accuracy;
    private List<KnowledgePointStatVO> recentWrongKnowledgePoints;
    private List<String> suggestions;
}
