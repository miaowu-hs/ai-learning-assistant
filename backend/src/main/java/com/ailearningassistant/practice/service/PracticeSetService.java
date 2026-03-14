package com.ailearningassistant.practice.service;

import com.ailearningassistant.practice.entity.PracticeSet;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PracticeSetService extends IService<PracticeSet> {

    PracticeSet getMyPracticeSet(Long id);
}
