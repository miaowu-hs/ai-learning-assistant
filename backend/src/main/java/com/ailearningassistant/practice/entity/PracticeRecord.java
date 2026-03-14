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
@TableName("practice_record")
public class PracticeRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("practice_set_id")
    private Long practiceSetId;

    @TableField("user_id")
    private Long userId;

    @TableField("total_score")
    private Integer totalScore;

    @TableField("objective_score")
    private Integer objectiveScore;

    @TableField("subjective_score")
    private Integer subjectiveScore;

    private String status;

    @TableField("submitted_at")
    private LocalDateTime submittedAt;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
