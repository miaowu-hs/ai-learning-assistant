CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    email VARCHAR(128) DEFAULT NULL,
    phone VARCHAR(32) DEFAULT NULL,
    status TINYINT NOT NULL DEFAULT 1,
    deleted TINYINT NOT NULL DEFAULT 0,
    last_login_time DATETIME DEFAULT NULL,
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_sys_user_username (username),
    KEY idx_sys_user_email (email),
    KEY idx_sys_user_phone (phone),
    KEY idx_sys_user_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='System user table';

CREATE TABLE IF NOT EXISTS learning_document (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_url VARCHAR(512) NOT NULL,
    file_type VARCHAR(32) NOT NULL,
    file_size BIGINT NOT NULL,
    parse_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    summary_status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_learning_document_user_id (user_id),
    KEY idx_learning_document_created_at (created_at),
    CONSTRAINT fk_learning_document_user_id FOREIGN KEY (user_id) REFERENCES sys_user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='Learning document table';

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

CREATE TABLE IF NOT EXISTS qa_session (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_qa_session_user_id (user_id),
    KEY idx_qa_session_document_id (document_id),
    CONSTRAINT fk_qa_session_document_id FOREIGN KEY (document_id) REFERENCES learning_document(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='QA session table';

CREATE TABLE IF NOT EXISTS qa_message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    session_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(16) NOT NULL,
    content LONGTEXT NOT NULL,
    references_json LONGTEXT DEFAULT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_qa_message_session_id (session_id),
    KEY idx_qa_message_user_id (user_id),
    CONSTRAINT fk_qa_message_session_id FOREIGN KEY (session_id) REFERENCES qa_session(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='QA message table';

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
