package com.ailearningassistant.practice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiJudgeResult {

    private Boolean correct;
    private Integer score;
    private String analysis;
}
