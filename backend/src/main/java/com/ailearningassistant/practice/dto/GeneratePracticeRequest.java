package com.ailearningassistant.practice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class GeneratePracticeRequest {

    @NotNull(message = "documentId must not be null")
    private Long documentId;

    @Min(value = 3, message = "questionCount must be >= 3")
    @Max(value = 20, message = "questionCount must be <= 20")
    private Integer questionCount = 6;

    @Size(max = 128, message = "title length must be <= 128")
    private String title;
}
