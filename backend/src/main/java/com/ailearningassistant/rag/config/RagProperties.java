package com.ailearningassistant.rag.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "rag.retrieval")
public class RagProperties {

    private int topK = 3;
    private int minScore = 1;
}
