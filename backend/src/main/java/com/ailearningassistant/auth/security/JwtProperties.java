package com.ailearningassistant.auth.security;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    @NotBlank(message = "JWT_SECRET_KEY must not be blank")
    @Size(min = 32, message = "JWT_SECRET_KEY must be at least 32 characters")
    private String secretKey;
    private Long expiration;
    private String issuer;
}
