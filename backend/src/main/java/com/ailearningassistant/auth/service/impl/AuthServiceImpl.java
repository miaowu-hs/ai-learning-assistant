package com.ailearningassistant.auth.service.impl;

import com.ailearningassistant.auth.dto.AuthResponse;
import com.ailearningassistant.auth.dto.LoginRequest;
import com.ailearningassistant.auth.dto.RegisterRequest;
import com.ailearningassistant.auth.model.LoginUser;
import com.ailearningassistant.auth.security.JwtTokenProvider;
import com.ailearningassistant.auth.service.AuthService;
import com.ailearningassistant.common.enums.StatusCode;
import com.ailearningassistant.common.exception.BusinessException;
import com.ailearningassistant.user.entity.SysUser;
import com.ailearningassistant.user.service.UserService;
import com.ailearningassistant.user.vo.UserProfileVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public UserProfileVO register(RegisterRequest request) {
        SysUser user = userService.register(request);
        return UserProfileVO.fromEntity(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        SysUser user = userService.getByUsername(request.getUsername());
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(StatusCode.USERNAME_OR_PASSWORD_ERROR);
        }
        if (!Integer.valueOf(1).equals(user.getStatus())) {
            throw new BusinessException(StatusCode.ACCOUNT_DISABLED);
        }

        userService.updateLastLoginTime(user.getId());
        user = userService.getById(user.getId());

        LoginUser loginUser = new LoginUser(user);
        String token = jwtTokenProvider.createToken(loginUser);
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getExpiration())
                .user(UserProfileVO.fromEntity(user))
                .build();
    }
}
