package com.ailearningassistant.practice.vo;

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
public class PracticeResultVO {

    private Long recordId;
    private Long practiceSetId;
    private Long documentId;
    private String title;
    private Integer totalScore;
    private Integer objectiveScore;
    private Integer subjectiveScore;
    private String status;
    private LocalDateTime submittedAt;
    private List<PracticeAnswerResultVO> answers;
}
