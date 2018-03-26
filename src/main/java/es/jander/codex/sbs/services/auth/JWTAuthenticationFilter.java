package es.jander.codex.sbs.services.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jander.codex.sbs.data.UserInfo;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;

@Slf4j
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter
{
    private AuthenticationManager authenticationManager;
    private AuthProperties authProperties;
    private ObjectMapper objectMapper = new ObjectMapper();

    public JWTAuthenticationFilter (AuthenticationManager authenticationManager, AuthProperties authProperties)
    {
        this.authenticationManager = authenticationManager;
        this.authProperties = authProperties;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response) throws AuthenticationException
    {
        try {
            UserInfo userInfo = objectMapper.readValue(request.getInputStream(), UserInfo.class);
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userInfo.getUsername(),
                            userInfo.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain,
            Authentication authResult) throws IOException, ServletException
    {
        String subject = ((UserAuthorizationDetails) authResult.getPrincipal()).getUsername();
        Date expiration = new Date(Instant.now().toEpochMilli() + authProperties.getExpiration());

        String token = Jwts.builder()
                .setSubject(subject)
                .setExpiration(expiration)
                .signWith(SignatureAlgorithm.HS512, authProperties.getSecret().getBytes())
                .compact();
        response.addHeader(authProperties.getHeaderString(), authProperties.getTokenPrefix() + token);
    }
}
