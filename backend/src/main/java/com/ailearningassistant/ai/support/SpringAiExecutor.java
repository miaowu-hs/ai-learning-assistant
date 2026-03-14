package com.ailearningassistant.ai.support;

import com.ailearningassistant.ai.prompt.AiPrompt;
import com.ailearningassistant.ai.prompt.AiPromptTemplateService;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SpringAiExecutor {

    private final ChatClient chatClient;
    private final AiPromptTemplateService aiPromptTemplateService;

    public String call(AiPrompt systemPrompt,
                       Map<String, ?> systemVariables,
                       AiPrompt userPrompt,
                       Map<String, ?> userVariables) {
        String renderedSystem = aiPromptTemplateService.render(systemPrompt, systemVariables);
        String renderedUser = aiPromptTemplateService.render(userPrompt, userVariables);
        return chatClient.prompt()
                .system(renderedSystem)
                .user(renderedUser)
                .call()
                .content();
    }
}
