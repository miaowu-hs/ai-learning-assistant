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
public class PracticeQuestionVO {

    private Long id;
    private String questionType;
    private String stem;
    private List<PracticeOptionVO> options;
    private Integer score;
    private Integer sortOrder;
}
