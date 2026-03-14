package com.ailearningassistant.ai.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SummaryPayload {

    private String shortSummary;
    private List<String> outline;
    private List<String> keyPoints;
}
