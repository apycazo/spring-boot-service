package es.jander.codex.sbs.services.auth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties
{
    private String secret = "35y3v53892242cvhp4";
    private long expiration = 3_600_000; // one hour
    private String tokenPrefix = "Bearer ";
    private String headerString = "Authorization";
    private String authenticationEndpoint = "/login";
}
