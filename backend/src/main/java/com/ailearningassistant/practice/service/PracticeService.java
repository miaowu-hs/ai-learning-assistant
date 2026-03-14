package com.ailearningassistant.practice.service;

import com.ailearningassistant.practice.dto.GeneratePracticeRequest;
import com.ailearningassistant.practice.dto.SubmitPracticeRequest;
import com.ailearningassistant.practice.vo.PracticeResultVO;
import com.ailearningassistant.practice.vo.PracticeSetDetailVO;

public interface PracticeService {

    PracticeSetDetailVO generatePractice(GeneratePracticeRequest request);

    PracticeSetDetailVO getPracticeDetail(Long practiceSetId);

    PracticeResultVO submitPractice(Long practiceSetId, SubmitPracticeRequest request);

    PracticeResultVO getPracticeResult(Long recordId);
}
