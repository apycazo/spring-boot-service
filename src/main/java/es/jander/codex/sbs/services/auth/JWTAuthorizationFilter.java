package es.jander.codex.sbs.services.auth;

import io.jsonwebtoken.Jwts;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter
{
    private AuthProperties authProperties;

    public JWTAuthorizationFilter (AuthenticationManager authManager, AuthProperties authProperties)
    {
        super(authManager);
        this.authProperties = authProperties;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain) throws IOException, ServletException
    {
        String header = request.getHeader(authProperties.getHeaderString());

        if (header == null || !header.startsWith(authProperties.getTokenPrefix())) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, authProperties.getAuthenticationEndpoint());
            chain.doFilter(request, response);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);

        if (authentication == null) {
            response.setHeader(HttpHeaders.WWW_AUTHENTICATE, authProperties.getAuthenticationEndpoint());
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request)
    {
        String token = request.getHeader(authProperties.getHeaderString());
        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(authProperties.getSecret().getBytes())
                    .parseClaimsJws(token.replace(authProperties.getTokenPrefix(), ""))
                    .getBody()
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
            return null;
        }
        return null;
    }
}
