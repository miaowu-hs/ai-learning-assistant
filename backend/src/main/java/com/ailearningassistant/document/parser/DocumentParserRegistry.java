package com.ailearningassistant.document.parser;

import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import java.util.List;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DocumentParserRegistry {

    private final List<DocumentParser> parsers;

    public DocumentParser getParser(String fileType) {
        String normalizedFileType = fileType == null ? "" : fileType.toLowerCase(Locale.ROOT);
        return parsers.stream()
                .filter(parser -> parser.supports(normalizedFileType))
                .findFirst()
                .orElseThrow(() -> new BusinessException(StatusCode.DOCUMENT_PARSER_NOT_FOUND));
    }
}
