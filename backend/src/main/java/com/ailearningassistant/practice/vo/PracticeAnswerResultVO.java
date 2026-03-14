package com.ailearningassistant.practice.vo;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PracticeAnswerResultVO {

    private Long questionId;
    private String questionType;
    private String stem;
    private List<PracticeOptionVO> options;
    private String userAnswer;
    private String correctAnswer;
    private Boolean correct;
    private Integer score;
    private Integer maxScore;
    private String judgeAnalysis;
    private String explanation;
}
