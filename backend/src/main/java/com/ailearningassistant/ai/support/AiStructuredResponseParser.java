package com.ailearningassistant.ai.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class AiStructuredResponseParser {

    private final ObjectMapper objectMapper;

    public <T> String formatInstructions(Class<T> targetType) {
        return new BeanOutputConverter<>(targetType).getFormat();
    }

    public <T> String formatInstructions(ParameterizedTypeReference<T> targetType) {
        return new BeanOutputConverter<>(targetType).getFormat();
    }

    public <T> T parse(String rawContent, Class<T> targetType) {
        BeanOutputConverter<T> converter = new BeanOutputConverter<>(targetType);
        return parseInternal(rawContent, converter, objectMapper.getTypeFactory().constructType(targetType));
    }

    public <T> T parse(String rawContent, ParameterizedTypeReference<T> targetType) {
        BeanOutputConverter<T> converter = new BeanOutputConverter<>(targetType);
        return parseInternal(rawContent, converter, objectMapper.getTypeFactory().constructType(targetType.getType()));
    }

    private <T> T parseInternal(String rawContent, BeanOutputConverter<T> converter, JavaType javaType) {
        if (!StringUtils.hasText(rawContent)) {
            throw new IllegalArgumentException("Model response is empty");
        }

        List<String> candidates = buildCandidates(rawContent);
        for (String candidate : candidates) {
            try {
                return converter.convert(candidate);
            } catch (Exception ignored) {
            }
            try {
                return objectMapper.readValue(candidate, javaType);
            } catch (Exception ignored) {
            }
        }
        throw new IllegalArgumentException("Unable to parse model response into target structure");
    }

    private List<String> buildCandidates(String rawContent) {
        List<String> candidates = new ArrayList<>();
        String trimmed = rawContent.trim();
        candidates.add(trimmed);

        String fenced = extractMarkdownJson(trimmed);
        if (StringUtils.hasText(fenced)) {
            candidates.add(fenced);
        }

        String jsonObject = extractJsonByBoundary(trimmed, '{', '}');
        if (StringUtils.hasText(jsonObject)) {
            candidates.add(jsonObject);
        }

        String jsonArray = extractJsonByBoundary(trimmed, '[', ']');
        if (StringUtils.hasText(jsonArray)) {
            candidates.add(jsonArray);
        }
        return candidates.stream().distinct().toList();
    }

    private String extractMarkdownJson(String content) {
        int start = content.indexOf("```json");
        if (start < 0) {
            start = content.indexOf("```");
        }
        if (start < 0) {
            return null;
        }
        int firstLineEnd = content.indexOf('\n', start);
        int end = content.indexOf("```", firstLineEnd < 0 ? start + 3 : firstLineEnd);
        if (end < 0) {
            return null;
        }
        return content.substring(firstLineEnd + 1, end).trim();
    }

    private String extractJsonByBoundary(String content, char startChar, char endChar) {
        int start = content.indexOf(startChar);
        int end = content.lastIndexOf(endChar);
        if (start < 0 || end <= start) {
            return null;
        }
        return content.substring(start, end + 1).trim();
    }
}
