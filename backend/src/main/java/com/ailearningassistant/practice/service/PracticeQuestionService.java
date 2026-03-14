package com.ailearningassistant.practice.service;

import com.ailearningassistant.practice.entity.PracticeQuestion;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PracticeQuestionService extends IService<PracticeQuestion> {

    void replaceQuestions(Long practiceSetId, List<PracticeQuestion> questions);

    List<PracticeQuestion> listByPracticeSetId(Long practiceSetId);
}
