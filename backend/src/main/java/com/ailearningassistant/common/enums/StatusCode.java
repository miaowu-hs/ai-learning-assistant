package com.ailearningassistant.common.enums;

import lombok.Getter;

@Getter
public enum StatusCode {

    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Resource not found"),
    SYSTEM_ERROR(500, "Internal server error"),

    VALIDATION_ERROR(1001, "Validation failed"),
    USERNAME_ALREADY_EXISTS(2001, "Username already exists"),
    USER_NOT_FOUND(2002, "User does not exist"),
    USERNAME_OR_PASSWORD_ERROR(2003, "Username or password is incorrect"),
    ACCOUNT_DISABLED(2004, "Account is disabled"),
    TOKEN_INVALID(2005, "Token is invalid or expired"),

    DOCUMENT_NOT_FOUND(3001, "Document not found"),
    UNSUPPORTED_FILE_TYPE(3002, "Unsupported file type"),
    FILE_UPLOAD_FAILED(3003, "File upload failed"),
    FILE_DELETE_FAILED(3004, "File delete failed"),
    DOCUMENT_PARSER_NOT_FOUND(3005, "No parser available for this document type"),
    DOCUMENT_PARSE_FAILED(3006, "Document parse failed"),
    DOCUMENT_CONTENT_EMPTY(3007, "Document content is empty after parsing"),
    DOCUMENT_SUMMARY_NOT_FOUND(3008, "Document summary not found"),
    DOCUMENT_SUMMARY_FAILED(3009, "Document summary generation failed"),
    DOCUMENT_NOT_PARSED(3010, "Document has not been parsed yet"),

    QA_SESSION_NOT_FOUND(4001, "QA session not found"),
    QA_QUESTION_EMPTY(4002, "Question must not be empty"),
    QA_GENERATE_FAILED(4003, "QA answer generation failed"),
    QA_MESSAGE_SAVE_FAILED(4004, "QA message save failed"),

    PRACTICE_SET_NOT_FOUND(5001, "Practice set not found"),
    PRACTICE_RECORD_NOT_FOUND(5002, "Practice record not found"),
    PRACTICE_GENERATE_FAILED(5003, "Practice generation failed"),
    PRACTICE_SUBMIT_FAILED(5004, "Practice submission failed"),
    PRACTICE_TYPE_UNSUPPORTED(5005, "Practice question type is unsupported"),
    PRACTICE_QUESTION_NOT_FOUND(5006, "Practice question not found"),
    PRACTICE_JUDGE_FAILED(5007, "Practice judge failed"),

    WRONG_QUESTION_NOT_FOUND(6001, "Wrong question not found"),
    WRONG_QUESTION_REGENERATE_FAILED(6002, "Failed to regenerate practice from wrong questions"),

    REPORT_GENERATE_FAILED(7001, "Failed to generate learning report");

    private final Integer code;
    private final String message;

    StatusCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
