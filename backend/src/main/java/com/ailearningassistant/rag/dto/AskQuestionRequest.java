package com.ailearningassistant.rag.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AskQuestionRequest {

    @NotBlank(message = "question must not be blank")
    @Size(max = 2000, message = "question length must be <= 2000")
    private String question;
}
