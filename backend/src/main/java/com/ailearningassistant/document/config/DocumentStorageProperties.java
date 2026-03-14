package com.ailearningassistant.document.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "document.storage")
public class DocumentStorageProperties {

    private String uploadDir = "uploads";
    private List<String> allowedExtensions = new ArrayList<>(List.of("pdf", "docx", "txt", "md"));
}
