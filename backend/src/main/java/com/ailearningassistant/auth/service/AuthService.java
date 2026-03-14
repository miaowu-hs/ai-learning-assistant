package com.ailearningassistant.auth.service;

import com.ailearningassistant.auth.dto.AuthResponse;
import com.ailearningassistant.auth.dto.LoginRequest;
import com.ailearningassistant.auth.dto.RegisterRequest;
import com.ailearningassistant.user.vo.UserProfileVO;

public interface AuthService {

    UserProfileVO register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
