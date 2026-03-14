package com.ailearningassistant.document.parser.impl;

import com.ailearningassistant.document.parser.DocumentParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.stereotype.Component;

@Component
public class PlainTextDocumentParser implements DocumentParser {

    @Override
    public boolean supports(String fileType) {
        return "txt".equalsIgnoreCase(fileType) || "md".equalsIgnoreCase(fileType);
    }

    @Override
    public String parse(Path filePath) throws IOException {
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }
}
