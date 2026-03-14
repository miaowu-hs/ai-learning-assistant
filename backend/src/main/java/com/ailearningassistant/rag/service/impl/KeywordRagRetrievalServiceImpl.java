package com.ailearningassistant.rag.service.impl;

import com.ailearningassistant.document.entity.DocumentChunk;
import com.ailearningassistant.document.service.DocumentChunkService;
import com.ailearningassistant.rag.config.RagProperties;
import com.ailearningassistant.rag.service.RagRetrievalService;
import com.ailearningassistant.rag.vo.ChunkReferenceVO;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KeywordRagRetrievalServiceImpl implements RagRetrievalService {

    private final DocumentChunkService documentChunkService;
    private final RagProperties ragProperties;

    @Override
    public List<ChunkReferenceVO> retrieveRelevantChunks(Long documentId, Long userId, String question) {
        List<DocumentChunk> chunks = documentChunkService.listByDocumentId(documentId, userId);
        String normalizedQuestion = normalize(question);
        Set<String> tokens = extractTokens(normalizedQuestion);

        List<ScoredChunk> scoredChunks = chunks.stream()
                .map(chunk -> new ScoredChunk(chunk, scoreChunk(chunk.getChunkContent(), normalizedQuestion, tokens)))
                .filter(item -> item.score() >= ragProperties.getMinScore())
                .sorted(Comparator.comparingInt(ScoredChunk::score).reversed()
                        .thenComparing(item -> item.chunk().getChunkIndex()))
                .limit(ragProperties.getTopK())
                .toList();

        if (scoredChunks.isEmpty()) {
            return chunks.stream()
                    .limit(ragProperties.getTopK())
                    .map(chunk -> ChunkReferenceVO.fromEntity(chunk, 0))
                    .toList();
        }

        return scoredChunks.stream()
                .map(item -> ChunkReferenceVO.fromEntity(item.chunk(), item.score()))
                .toList();
    }

    private String normalize(String question) {
        return question == null ? "" : question.toLowerCase(Locale.ROOT).trim();
    }

    private Set<String> extractTokens(String question) {
        Set<String> tokens = new LinkedHashSet<>();
        for (String token : question.split("[\\p{Punct}\\s]+")) {
            if (token.length() >= 2) {
                tokens.add(token);
            }
        }
        String compactQuestion = question.replaceAll("\\s+", "");
        if (compactQuestion.length() >= 2) {
            for (int i = 0; i < compactQuestion.length() - 1; i++) {
                tokens.add(compactQuestion.substring(i, i + 2));
            }
        }
        if (tokens.isEmpty() && !compactQuestion.isBlank()) {
            tokens.add(compactQuestion);
        }
        return tokens;
    }

    private int scoreChunk(String content, String normalizedQuestion, Set<String> tokens) {
        String normalizedContent = content == null ? "" : content.toLowerCase(Locale.ROOT);
        int score = 0;
        if (!normalizedQuestion.isBlank() && normalizedContent.contains(normalizedQuestion)) {
            score += normalizedQuestion.length() * 3;
        }
        for (String token : tokens) {
            score += countOccurrences(normalizedContent, token) * Math.max(token.length(), 1);
        }
        return score;
    }

    private int countOccurrences(String content, String token) {
        if (token.isBlank()) {
            return 0;
        }
        int count = 0;
        int fromIndex = 0;
        while ((fromIndex = content.indexOf(token, fromIndex)) >= 0) {
            count++;
            fromIndex += token.length();
        }
        return count;
    }

    private record ScoredChunk(DocumentChunk chunk, int score) {
    }
}
