package com.ailearningassistant.practice.service;

import com.ailearningassistant.practice.entity.PracticeAnswer;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

public interface PracticeAnswerService extends IService<PracticeAnswer> {

    void saveAnswers(List<PracticeAnswer> answers);

    List<PracticeAnswer> listByRecordId(Long recordId);
}
