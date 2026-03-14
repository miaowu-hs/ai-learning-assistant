package com.ailearningassistant.wrongbook.controller;

import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.practice.vo.PracticeSetDetailVO;
import com.ailearningassistant.wrongbook.dto.RegenerateWrongPracticeRequest;
import com.ailearningassistant.wrongbook.service.WrongQuestionBookService;
import com.ailearningassistant.wrongbook.vo.WrongQuestionVO;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/wrongbook")
@RequiredArgsConstructor
public class WrongQuestionBookController {

    private final WrongQuestionBookService wrongQuestionBookService;

    @GetMapping
    public Result<List<WrongQuestionVO>> listMine() {
        return Result.success(wrongQuestionBookService.listMyWrongQuestions());
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        wrongQuestionBookService.deleteMyWrongQuestion(id);
        return Result.success();
    }

    @PostMapping("/regenerate")
    public Result<PracticeSetDetailVO> regenerate(@Valid @RequestBody RegenerateWrongPracticeRequest request) {
        return Result.success(wrongQuestionBookService.regeneratePractice(request));
    }
}
