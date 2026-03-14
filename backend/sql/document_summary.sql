USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS document_summary (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    short_summary TEXT NOT NULL,
    outline LONGTEXT NOT NULL,
    key_points LONGTEXT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_document_summary_document_id (document_id),
    KEY idx_document_summary_user_id (user_id),
    CONSTRAINT fk_document_summary_document_id FOREIGN KEY (document_id) REFERENCES learning_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Document summary table';
