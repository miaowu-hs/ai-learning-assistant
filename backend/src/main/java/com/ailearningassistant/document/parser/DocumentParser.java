package com.ailearningassistant.document.parser;

import java.io.IOException;
import java.nio.file.Path;

public interface DocumentParser {

    boolean supports(String fileType);

    String parse(Path filePath) throws IOException;
}
