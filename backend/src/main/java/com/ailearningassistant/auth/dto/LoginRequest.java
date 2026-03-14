package com.ailearningassistant.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "username must not be blank")
    @Size(max = 64, message = "username length must be <= 64")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Size(max = 64, message = "password length must be <= 64")
    private String password;
}
