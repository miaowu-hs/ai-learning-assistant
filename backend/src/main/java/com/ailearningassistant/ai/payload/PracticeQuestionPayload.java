package com.ailearningassistant.ai.payload;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PracticeQuestionPayload {

    private String questionType;
    private String stem;
    private List<PracticeOptionPayload> options;
    private String correctAnswer;
    private String explanation;
    private Integer score;
    private Integer sortOrder;
}
