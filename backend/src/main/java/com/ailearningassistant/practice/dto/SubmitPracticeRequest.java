package com.ailearningassistant.practice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.Data;

@Data
public class SubmitPracticeRequest {

    @Valid
    @NotEmpty(message = "answers must not be empty")
    private List<SubmitPracticeAnswerItem> answers;
}
