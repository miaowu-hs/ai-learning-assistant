package com.ailearningassistant.document.service;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.document.config.DocumentProcessingProperties;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class DocumentTextProcessingService {

    private final DocumentProcessingProperties documentProcessingProperties;

    public String cleanText(String rawText) {
        if (!StringUtils.hasText(rawText)) {
            throw new BusinessException(StatusCode.DOCUMENT_CONTENT_EMPTY);
        }

        String cleanedText = rawText
                .replace("\u0000", "")
                .replace("\r\n", "\n")
                .replace("\r", "\n")
                .replaceAll("[\\t\\x0B\\f]+", " ")
                .replaceAll("[ ]{2,}", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();

        if (!StringUtils.hasText(cleanedText)) {
            throw new BusinessException(StatusCode.DOCUMENT_CONTENT_EMPTY);
        }
        return cleanedText;
    }

    public List<String> splitIntoChunks(String text) {
        if (!StringUtils.hasText(text)) {
            throw new BusinessException(StatusCode.DOCUMENT_CONTENT_EMPTY);
        }

        int chunkSize = Math.max(documentProcessingProperties.getChunkSize(), 200);
        List<String> chunks = new ArrayList<>();
        int start = 0;
        int length = text.length();

        while (start < length) {
            int end = Math.min(start + chunkSize, length);
            if (end < length) {
                int adjustedEnd = findBreakPoint(text, start, end, chunkSize);
                if (adjustedEnd > start) {
                    end = adjustedEnd;
                }
            }

            String chunk = text.substring(start, end).trim();
            if (StringUtils.hasText(chunk)) {
                chunks.add(chunk);
            }

            start = end;
            while (start < length && Character.isWhitespace(text.charAt(start))) {
                start++;
            }
        }

        if (chunks.isEmpty()) {
            throw new BusinessException(StatusCode.DOCUMENT_CONTENT_EMPTY);
        }
        return chunks;
    }

    private int findBreakPoint(String text, int start, int end, int chunkSize) {
        int minAcceptable = start + (int) (chunkSize * 0.6);
        for (int i = end - 1; i >= minAcceptable; i--) {
            char current = text.charAt(i);
            if (current == '\n' || current == '.' || current == '!' || current == '?'
                    || current == '。' || current == '！' || current == '？' || current == ';' || current == '；') {
                return i + 1;
            }
        }
        return end;
    }
}
