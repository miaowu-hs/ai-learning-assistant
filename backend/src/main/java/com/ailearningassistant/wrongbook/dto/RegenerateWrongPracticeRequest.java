package com.ailearningassistant.wrongbook.dto;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.Data;

@Data
public class RegenerateWrongPracticeRequest {

    private Long documentId;

    private List<Long> wrongQuestionIds;

    @Size(max = 128, message = "title length must be <= 128")
    private String title;
}
