package com.ailearningassistant.document.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "document.processing")
public class DocumentProcessingProperties {

    private int chunkSize = 1000;
}
