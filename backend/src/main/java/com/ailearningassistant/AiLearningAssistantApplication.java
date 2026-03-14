package com.ailearningassistant;

import com.ailearningassistant.ai.config.AppAiProperties;
import com.ailearningassistant.document.config.DocumentStorageProperties;
import com.ailearningassistant.document.config.DocumentProcessingProperties;
import com.ailearningassistant.rag.config.RagProperties;
import com.ailearningassistant.auth.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        AppAiProperties.class,
        DocumentStorageProperties.class,
        DocumentProcessingProperties.class,
        RagProperties.class
})
public class AiLearningAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiLearningAssistantApplication.class, args);
    }
}
