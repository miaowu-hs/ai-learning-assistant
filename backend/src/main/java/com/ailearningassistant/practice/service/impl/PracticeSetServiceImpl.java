package com.ailearningassistant.practice.service.impl;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.practice.entity.PracticeSet;
import com.ailearningassistant.practice.mapper.PracticeSetMapper;
import com.ailearningassistant.practice.service.PracticeSetService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class PracticeSetServiceImpl extends ServiceImpl<PracticeSetMapper, PracticeSet> implements PracticeSetService {

    @Override
    public PracticeSet getMyPracticeSet(Long id) {
        PracticeSet practiceSet = lambdaQuery()
                .eq(PracticeSet::getId, id)
                .eq(PracticeSet::getUserId, SecurityUtils.getUserId())
                .one();
        if (practiceSet == null) {
            throw new BusinessException(StatusCode.PRACTICE_SET_NOT_FOUND);
        }
        return practiceSet;
    }
}
