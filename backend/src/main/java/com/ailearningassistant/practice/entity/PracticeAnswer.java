package com.ailearningassistant.practice.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("practice_answer")
public class PracticeAnswer {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("practice_record_id")
    private Long practiceRecordId;

    @TableField("question_id")
    private Long questionId;

    @TableField("user_id")
    private Long userId;

    @TableField("user_answer")
    private String userAnswer;

    @TableField("is_correct")
    private Integer isCorrect;

    private Integer score;

    @TableField("judge_analysis")
    private String judgeAnalysis;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
