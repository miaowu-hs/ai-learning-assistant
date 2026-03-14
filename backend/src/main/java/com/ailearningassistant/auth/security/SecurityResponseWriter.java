package com.ailearningassistant.auth.security;

import com.ailearningassistant.common.api.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class SecurityResponseWriter {

    private SecurityResponseWriter() {
    }

    public static void write(HttpServletResponse response, ObjectMapper objectMapper, Result<?> result)
            throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
