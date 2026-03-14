package com.ailearningassistant.practice.controller;

import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.practice.dto.GeneratePracticeRequest;
import com.ailearningassistant.practice.dto.SubmitPracticeRequest;
import com.ailearningassistant.practice.service.PracticeService;
import com.ailearningassistant.practice.vo.PracticeResultVO;
import com.ailearningassistant.practice.vo.PracticeSetDetailVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/practices")
@RequiredArgsConstructor
public class PracticeController {

    private final PracticeService practiceService;

    @PostMapping("/generate")
    public Result<PracticeSetDetailVO> generate(@Valid @RequestBody GeneratePracticeRequest request) {
        return Result.success(practiceService.generatePractice(request));
    }

    @GetMapping("/sets/{practiceSetId}")
    public Result<PracticeSetDetailVO> getDetail(@PathVariable Long practiceSetId) {
        return Result.success(practiceService.getPracticeDetail(practiceSetId));
    }

    @PostMapping("/sets/{practiceSetId}/submit")
    public Result<PracticeResultVO> submit(@PathVariable Long practiceSetId,
                                           @Valid @RequestBody SubmitPracticeRequest request) {
        return Result.success(practiceService.submitPractice(practiceSetId, request));
    }

    @GetMapping("/records/{recordId}")
    public Result<PracticeResultVO> getResult(@PathVariable Long recordId) {
        return Result.success(practiceService.getPracticeResult(recordId));
    }
}
