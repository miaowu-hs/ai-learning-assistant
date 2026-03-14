package com.ailearningassistant.auth.security;

import com.ailearningassistant.auth.model.LoginUser;
import com.ailearningassistant.auth.service.CustomUserDetailsService;
import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.common.enums.StatusCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.hasText(authorization) || !authorization.startsWith(TOKEN_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring(TOKEN_PREFIX.length());
        try {
            if (!jwtTokenProvider.validateToken(token)) {
                SecurityResponseWriter.write(response, objectMapper, Result.error(StatusCode.TOKEN_INVALID));
                return;
            }

            String username = jwtTokenProvider.getUsername(token);
            if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {
                LoginUser loginUser = customUserDetailsService.loadLoginUserByUsername(username);
                if (!loginUser.isEnabled()) {
                    SecurityResponseWriter.write(response, objectMapper, Result.error(StatusCode.ACCOUNT_DISABLED));
                    return;
                }

                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException ex) {
            SecurityResponseWriter.write(response, objectMapper, Result.error(StatusCode.TOKEN_INVALID));
        }
    }
}
