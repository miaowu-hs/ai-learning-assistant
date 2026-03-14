package com.ailearningassistant.practice.vo;

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
public class PracticeSetDetailVO {

    private Long id;
    private Long documentId;
    private String title;
    private Integer questionCount;
    private Integer totalScore;
    private LocalDateTime createdAt;
    private List<PracticeQuestionVO> questions;
}
