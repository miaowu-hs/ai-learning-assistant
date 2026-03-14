USE ai_learning_assistant;

CREATE TABLE IF NOT EXISTS document_chunk (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    document_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    chunk_index INT NOT NULL,
    chunk_content LONGTEXT NOT NULL,
    chunk_length INT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_document_chunk_document_index (document_id, chunk_index),
    KEY idx_document_chunk_user_id (user_id),
    CONSTRAINT fk_document_chunk_document_id FOREIGN KEY (document_id) REFERENCES learning_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Document chunk table';
