package com.ailearningassistant.practice.service.impl;

import com.ailearningassistant.practice.entity.PracticeQuestion;
import com.ailearningassistant.practice.mapper.PracticeQuestionMapper;
import com.ailearningassistant.practice.service.PracticeQuestionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PracticeQuestionServiceImpl extends ServiceImpl<PracticeQuestionMapper, PracticeQuestion>
        implements PracticeQuestionService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceQuestions(Long practiceSetId, List<PracticeQuestion> questions) {
        remove(new LambdaQueryWrapper<PracticeQuestion>().eq(PracticeQuestion::getPracticeSetId, practiceSetId));
        if (!questions.isEmpty()) {
            saveBatch(questions);
        }
    }

    @Override
    public List<PracticeQuestion> listByPracticeSetId(Long practiceSetId) {
        return lambdaQuery()
                .eq(PracticeQuestion::getPracticeSetId, practiceSetId)
                .orderByAsc(PracticeQuestion::getSortOrder)
                .list();
    }
}
