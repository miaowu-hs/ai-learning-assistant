USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS practice_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    practice_set_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    total_score INT NOT NULL DEFAULT 0,
    objective_score INT NOT NULL DEFAULT 0,
    subjective_score INT NOT NULL DEFAULT 0,
    status VARCHAR(32) NOT NULL DEFAULT 'SUBMITTED',
    submitted_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_practice_record_set_id (practice_set_id),
    KEY idx_practice_record_user_id (user_id),
    CONSTRAINT fk_practice_record_set_id FOREIGN KEY (practice_set_id) REFERENCES practice_set(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Practice record table';
