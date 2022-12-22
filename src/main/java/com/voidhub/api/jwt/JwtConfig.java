package com.voidhub.api.jwt;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "application.jwt")
@Component
@NoArgsConstructor
@Getter
@Setter
public class JwtConfig {

    private String secretKey;
    private String tokenPrefix;
    private int tokenExpirationAfterDays;

    public String getAuthorizationHeader() {
        return "Authorization";
    }

}
