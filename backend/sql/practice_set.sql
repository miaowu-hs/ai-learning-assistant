USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS practice_set (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    question_count INT NOT NULL DEFAULT 0,
    total_score INT NOT NULL DEFAULT 0,
    generation_payload LONGTEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_practice_set_user_id (user_id),
    KEY idx_practice_set_document_id (document_id),
    CONSTRAINT fk_practice_set_document_id FOREIGN KEY (document_id) REFERENCES learning_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Practice set table';
