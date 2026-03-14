package com.ailearningassistant.ai.service;

import com.ailearningassistant.practice.entity.PracticeQuestion;
import com.ailearningassistant.practice.model.AiJudgeResult;

public interface AiJudgeService {

    AiJudgeResult judgeShortAnswer(PracticeQuestion question, String userAnswer);
}
