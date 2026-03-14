USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS practice_question (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    practice_set_id BIGINT NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    stem TEXT NOT NULL,
    options_json LONGTEXT DEFAULT NULL,
    correct_answer TEXT NOT NULL,
    explanation TEXT DEFAULT NULL,
    score INT NOT NULL DEFAULT 10,
    sort_order INT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_practice_question_set_id (practice_set_id),
    CONSTRAINT fk_practice_question_set_id FOREIGN KEY (practice_set_id) REFERENCES practice_set(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Practice question table';
