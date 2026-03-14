package com.ailearningassistant.practice.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeneratedPracticeQuestion {

    private QuestionType questionType;
    private String stem;
    private List<PracticeOption> options;
    private String correctAnswer;
    private String explanation;
    private Integer score;
    private Integer sortOrder;
}
