package com.ailearningassistant.ai.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JudgePayload {

    private Boolean correct;
    private Integer score;
    private String analysis;
}
