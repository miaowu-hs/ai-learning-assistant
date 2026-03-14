package com.ailearningassistant.practice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SubmitPracticeAnswerItem {

    @NotNull(message = "questionId must not be null")
    private Long questionId;

    private String answer;
}
