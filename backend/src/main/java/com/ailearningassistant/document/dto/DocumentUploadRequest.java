package com.ailearningassistant.document.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class DocumentUploadRequest {

    @Size(max = 128, message = "title length must be <= 128")
    private String title;

    private MultipartFile file;
}
