package com.ailearningassistant.ai.prompt;

import com.ailearningassistant.ai.config.AppAiProperties;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiPromptTemplateService {

    private final ResourceLoader resourceLoader;
    private final AppAiProperties appAiProperties;
    private final Map<AiPrompt, String> cache = new EnumMap<>(AiPrompt.class);

    public String render(AiPrompt prompt, Map<String, ?> variables) {
        String template = cache.computeIfAbsent(prompt, this::loadTemplate);
        String rendered = template;
        if (variables != null) {
            for (Map.Entry<String, ?> entry : variables.entrySet()) {
                String placeholder = "{" + entry.getKey() + "}";
                String value = entry.getValue() == null ? "" : String.valueOf(entry.getValue());
                rendered = rendered.replace(placeholder, value);
            }
        }
        return rendered;
    }

    private String loadTemplate(AiPrompt prompt) {
        String location = normalizeBasePath(appAiProperties.getPromptsPath()) + prompt.getFileName();
        Resource resource = resourceLoader.getResource(location);
        try {
            return new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to load prompt template: " + location, ex);
        }
    }

    private String normalizeBasePath(String basePath) {
        if (basePath.endsWith("/")) {
            return basePath;
        }
        return basePath + "/";
    }
}
