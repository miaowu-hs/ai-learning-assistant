package com.ailearningassistant.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "username must not be blank")
    @Size(max = 64, message = "username length must be <= 64")
    private String username;

    @NotBlank(message = "password must not be blank")
    @Size(min = 6, max = 64, message = "password length must be between 6 and 64")
    private String password;

    @Size(max = 64, message = "nickname length must be <= 64")
    private String nickname;

    @Email(message = "email format is invalid")
    @Size(max = 128, message = "email length must be <= 128")
    private String email;

    @Size(max = 32, message = "phone length must be <= 32")
    private String phone;
}
