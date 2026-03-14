package com.ailearningassistant.auth.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secretKey;
    private Long expiration;
    private String issuer;
}
