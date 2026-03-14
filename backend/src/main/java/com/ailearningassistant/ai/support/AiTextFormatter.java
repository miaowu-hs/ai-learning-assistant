package com.ailearningassistant.ai.support;

import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import com.ailearningassistant.report.vo.KnowledgePointStatVO;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AiTextFormatter {

    public String formatChunks(List<String> chunks, int maxCount, int maxCharsEach) {
        if (chunks == null || chunks.isEmpty()) {
            return "No chunk content available.";
        }
        StringBuilder builder = new StringBuilder();
        int count = Math.min(chunks.size(), Math.max(1, maxCount));
        for (int i = 0; i < count; i++) {
            builder.append("Chunk ").append(i + 1).append(": ")
                    .append(abbreviate(chunks.get(i), maxCharsEach))
                    .append("\n");
        }
        return builder.toString().trim();
    }

    public String formatReferences(List<ChunkReferenceVO> references, int maxCount, int maxCharsEach) {
        if (references == null || references.isEmpty()) {
            return "No references available.";
        }
        StringBuilder builder = new StringBuilder();
        int count = Math.min(references.size(), Math.max(1, maxCount));
        for (int i = 0; i < count; i++) {
            ChunkReferenceVO reference = references.get(i);
            builder.append("Reference ").append(i + 1)
                    .append(" [chunk ").append(reference.getChunkIndex()).append(", score=")
                    .append(reference.getScore()).append("]: ")
                    .append(abbreviate(reference.getContent(), maxCharsEach))
                    .append("\n");
        }
        return builder.toString().trim();
    }

    public String formatKnowledgePoints(List<KnowledgePointStatVO> points, int maxCount) {
        if (points == null || points.isEmpty()) {
            return "No recent wrong knowledge points.";
        }
        StringBuilder builder = new StringBuilder();
        int count = Math.min(points.size(), Math.max(1, maxCount));
        for (int i = 0; i < count; i++) {
            KnowledgePointStatVO point = points.get(i);
            builder.append("- ").append(point.getKnowledgePoint())
                    .append(" (").append(point.getCount()).append(")\n");
        }
        return builder.toString().trim();
    }

    public String abbreviate(String value, int maxLength) {
        if (!StringUtils.hasText(value)) {
            return "";
        }
        String normalized = value.replaceAll("\\s+", " ").trim();
        if (normalized.length() <= maxLength) {
            return normalized;
        }
        return normalized.substring(0, maxLength) + "...";
    }
}
