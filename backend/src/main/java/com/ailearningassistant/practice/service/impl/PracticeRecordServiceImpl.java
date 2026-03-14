package com.ailearningassistant.practice.service.impl;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.common.util.SecurityUtils;
import com.ailearningassistant.practice.entity.PracticeRecord;
import com.ailearningassistant.practice.mapper.PracticeRecordMapper;
import com.ailearningassistant.practice.service.PracticeRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class PracticeRecordServiceImpl extends ServiceImpl<PracticeRecordMapper, PracticeRecord>
        implements PracticeRecordService {

    @Override
    public PracticeRecord createRecord(Long practiceSetId,
                                       Long userId,
                                       Integer totalScore,
                                       Integer objectiveScore,
                                       Integer subjectiveScore,
                                       String status) {
        LocalDateTime now = LocalDateTime.now();
        PracticeRecord record = PracticeRecord.builder()
                .practiceSetId(practiceSetId)
                .userId(userId)
                .totalScore(totalScore)
                .objectiveScore(objectiveScore)
                .subjectiveScore(subjectiveScore)
                .status(status)
                .submittedAt(now)
                .createdAt(now)
                .updatedAt(now)
                .build();
        boolean saved = save(record);
        if (!saved) {
            throw new BusinessException(StatusCode.PRACTICE_SUBMIT_FAILED);
        }
        return record;
    }

    @Override
    public PracticeRecord getMyRecord(Long recordId) {
        PracticeRecord record = lambdaQuery()
                .eq(PracticeRecord::getId, recordId)
                .eq(PracticeRecord::getUserId, SecurityUtils.getUserId())
                .one();
        if (record == null) {
            throw new BusinessException(StatusCode.PRACTICE_RECORD_NOT_FOUND);
        }
        return record;
    }
}
