USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS practice_answer (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    practice_record_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    user_answer TEXT DEFAULT NULL,
    is_correct TINYINT NOT NULL DEFAULT 0,
    score INT NOT NULL DEFAULT 0,
    judge_analysis TEXT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_practice_answer_record_id (practice_record_id),
    KEY idx_practice_answer_question_id (question_id),
    KEY idx_practice_answer_user_id (user_id),
    CONSTRAINT fk_practice_answer_record_id FOREIGN KEY (practice_record_id) REFERENCES practice_record(id) ON DELETE CASCADE,
    CONSTRAINT fk_practice_answer_question_id FOREIGN KEY (question_id) REFERENCES practice_question(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Practice answer table';
