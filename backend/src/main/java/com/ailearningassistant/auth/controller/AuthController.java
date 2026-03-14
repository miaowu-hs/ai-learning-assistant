package com.ailearningassistant.auth.controller;

import com.ailearningassistant.auth.dto.AuthResponse;
import com.ailearningassistant.auth.dto.LoginRequest;
import com.ailearningassistant.auth.dto.RegisterRequest;
import com.ailearningassistant.auth.service.AuthService;
import com.ailearningassistant.common.api.Result;
import com.ailearningassistant.user.vo.UserProfileVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public Result<UserProfileVO> register(@Valid @RequestBody RegisterRequest request) {
        return Result.success(authService.register(request));
    }

    @PostMapping("/login")
    public Result<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
