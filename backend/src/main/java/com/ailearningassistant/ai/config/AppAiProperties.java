package com.ailearningassistant.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.ai")
public class AppAiProperties {

    private String promptsPath = "classpath:/prompts/";
    private int summaryChunkLimit = 6;
    private int practiceChunkLimit = 8;
    private int suggestionWrongPointLimit = 5;
}
