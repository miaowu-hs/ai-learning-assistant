package com.ailearningassistant.ai.prompt;

public enum AiPrompt {
    SUMMARY_SYSTEM("summary-system.txt"),
    SUMMARY_USER("summary-user.txt"),
    CHAT_SYSTEM("chat-system.txt"),
    CHAT_USER("chat-user.txt"),
    PRACTICE_SYSTEM("practice-system.txt"),
    PRACTICE_USER("practice-user.txt"),
    JUDGE_SYSTEM("judge-system.txt"),
    JUDGE_USER("judge-user.txt"),
    SUGGESTION_SYSTEM("suggestion-system.txt"),
    SUGGESTION_USER("suggestion-user.txt");

    private final String fileName;

    AiPrompt(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() {
        return fileName;
    }
}
