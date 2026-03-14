package com.ailearningassistant.wrongbook.vo;

import com.ailearningassistant.practice.model.PracticeOption;
import com.ailearningassistant.practice.vo.PracticeOptionVO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WrongQuestionVO {

    private Long id;
    private Long documentId;
    private Long practiceSetId;
    private Long questionId;
    private String questionType;
    private String stem;
    private List<PracticeOptionVO> options;
    private String correctAnswer;
    private String explanation;
    private String knowledgePoint;
    private Integer wrongCount;
    private String lastUserAnswer;
    private LocalDateTime updatedAt;
}
