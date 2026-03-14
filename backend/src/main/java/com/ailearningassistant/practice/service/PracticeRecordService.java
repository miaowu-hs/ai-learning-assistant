package com.ailearningassistant.practice.service;

import com.ailearningassistant.practice.entity.PracticeRecord;
import com.baomidou.mybatisplus.extension.service.IService;

public interface PracticeRecordService extends IService<PracticeRecord> {

    PracticeRecord createRecord(Long practiceSetId, Long userId, Integer totalScore, Integer objectiveScore,
                                Integer subjectiveScore, String status);

    PracticeRecord getMyRecord(Long recordId);
}
