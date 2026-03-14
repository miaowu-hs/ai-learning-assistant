package com.ailearningassistant.practice.service.impl;

import com.ailearningassistant.practice.entity.PracticeAnswer;
import com.ailearningassistant.practice.mapper.PracticeAnswerMapper;
import com.ailearningassistant.practice.service.PracticeAnswerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PracticeAnswerServiceImpl extends ServiceImpl<PracticeAnswerMapper, PracticeAnswer>
        implements PracticeAnswerService {

    @Override
    public void saveAnswers(List<PracticeAnswer> answers) {
        if (!answers.isEmpty()) {
            saveBatch(answers);
        }
    }

    @Override
    public List<PracticeAnswer> listByRecordId(Long recordId) {
        return lambdaQuery()
                .eq(PracticeAnswer::getPracticeRecordId, recordId)
                .orderByAsc(PracticeAnswer::getId)
                .list();
    }
}
