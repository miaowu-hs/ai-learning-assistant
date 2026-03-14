package com.ailearningassistant.rag.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateQaSessionRequest {

    @NotNull(message = "documentId must not be null")
    private Long documentId;

    @Size(max = 128, message = "title length must be <= 128")
    private String title;
}
