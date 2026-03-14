USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS wrong_question_book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    practice_set_id BIGINT NOT NULL,
    question_id BIGINT NOT NULL,
    question_type VARCHAR(32) NOT NULL,
    stem TEXT NOT NULL,
    options_json LONGTEXT DEFAULT NULL,
    correct_answer TEXT NOT NULL,
    explanation TEXT DEFAULT NULL,
    knowledge_point VARCHAR(255) NOT NULL,
    wrong_count INT NOT NULL DEFAULT 1,
    last_user_answer TEXT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_wrong_question_user_question (user_id, question_id),
    KEY idx_wrong_question_user_id (user_id),
    KEY idx_wrong_question_document_id (document_id),
    CONSTRAINT fk_wrong_question_document_id FOREIGN KEY (document_id) REFERENCES learning_document(id) ON DELETE CASCADE,
    CONSTRAINT fk_wrong_question_question_id FOREIGN KEY (question_id) REFERENCES practice_question(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Wrong question book table';
