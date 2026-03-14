package com.ailearningassistant.rag.controller;

import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.rag.dto.AskQuestionRequest;
import com.ailearningassistant.rag.dto.CreateQaSessionRequest;
import com.ailearningassistant.rag.service.RagQaService;
import com.ailearningassistant.rag.vo.QaAskResponseVO;
import com.ailearningassistant.rag.vo.QaSessionHistoryVO;
import com.ailearningassistant.rag.vo.QaSessionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/qa/sessions")
@RequiredArgsConstructor
public class RagQaController {

    private final RagQaService ragQaService;

    @PostMapping
    public Result<QaSessionVO> createSession(@Valid @RequestBody CreateQaSessionRequest request) {
        return Result.success(ragQaService.createSession(request));
    }

    @PostMapping("/{sessionId}/ask")
    public Result<QaAskResponseVO> ask(@PathVariable Long sessionId, @Valid @RequestBody AskQuestionRequest request) {
        return Result.success(ragQaService.askQuestion(sessionId, request));
    }

    @GetMapping("/{sessionId}/history")
    public Result<QaSessionHistoryVO> getHistory(@PathVariable Long sessionId) {
        return Result.success(ragQaService.getSessionHistory(sessionId));
    }
}
