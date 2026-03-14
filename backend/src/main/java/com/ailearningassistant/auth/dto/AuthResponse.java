package com.ailearningassistant.auth.dto;

import com.ailearningassistant.user.vo.UserProfileVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private String tokenType;
    private Long expiresIn;
    private UserProfileVO user;
}
