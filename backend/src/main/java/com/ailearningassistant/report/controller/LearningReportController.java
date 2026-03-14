package com.ailearningassistant.report.controller;

import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.report.service.LearningReportService;
import com.ailearningassistant.report.vo.LearningReportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class LearningReportController {

    private final LearningReportService learningReportService;

    @GetMapping("/me")
    public Result<LearningReportVO> getMyReport() {
        return Result.success(learningReportService.getMyLearningReport());
    }
}
