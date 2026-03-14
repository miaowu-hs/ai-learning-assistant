package com.ailearningassistant.wrongbook.entity;

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
@TableName("wrong_question_book")
public class WrongQuestionBook {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("document_id")
    private Long documentId;

    @TableField("practice_set_id")
    private Long practiceSetId;

    @TableField("question_id")
    private Long questionId;

    @TableField("question_type")
    private String questionType;

    private String stem;

    @TableField("options_json")
    private String optionsJson;

    @TableField("correct_answer")
    private String correctAnswer;

    private String explanation;

    @TableField("knowledge_point")
    private String knowledgePoint;

    @TableField("wrong_count")
    private Integer wrongCount;

    @TableField("last_user_answer")
    private String lastUserAnswer;

    @TableField("created_at")
    private LocalDateTime createdAt;

    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
