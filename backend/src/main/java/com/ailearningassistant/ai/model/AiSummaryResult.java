package com.ailearningassistant.ai.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiSummaryResult {

    private String shortSummary;
    private List<String> outline;
    private List<String> keyPoints;
}
