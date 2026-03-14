package com.ailearningassistant.document.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class DocumentPageQueryRequest {

    @Min(value = 1, message = "pageNum must be >= 1")
    private long pageNum = 1;

    @Min(value = 1, message = "pageSize must be >= 1")
    @Max(value = 100, message = "pageSize must be <= 100")
    private long pageSize = 10;

    private String title;
}
