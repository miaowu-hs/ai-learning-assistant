package com.ailearningassistant.wrongbook.service;

import com.ailearningassistant.practice.entity.PracticeAnswer;
import com.ailearningassistant.practice.entity.PracticeQuestion;
import com.ailearningassistant.practice.entity.PracticeSet;
import com.ailearningassistant.practice.vo.PracticeSetDetailVO;
import com.ailearningassistant.wrongbook.dto.RegenerateWrongPracticeRequest;
import com.ailearningassistant.wrongbook.entity.WrongQuestionBook;
import com.ailearningassistant.wrongbook.vo.WrongQuestionVO;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface WrongQuestionBookService extends IService<WrongQuestionBook> {

    void recordWrongAnswers(PracticeSet practiceSet, List<PracticeQuestion> questions, List<PracticeAnswer> answers);

    List<WrongQuestionVO> listMyWrongQuestions();

    void deleteMyWrongQuestion(Long id);

    PracticeSetDetailVO regeneratePractice(RegenerateWrongPracticeRequest request);

    List<WrongQuestionBook> listRecentWrongQuestions(Long userId, int limit);
}
