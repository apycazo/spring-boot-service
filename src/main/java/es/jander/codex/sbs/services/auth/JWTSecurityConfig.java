package es.jander.codex.sbs.services.auth;

import es.jander.codex.sbs.services.dos.DosProtectionFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@ConditionalOnProperty(
        value = JWTSecurityConfig.SECURITY_ENABLER_PROPERTY_NAME,
        matchIfMissing = true,
        havingValue = "true")
public class JWTSecurityConfig extends WebSecurityConfigurerAdapter
{
    public static final String SECURITY_ENABLER_PROPERTY_NAME = "app.security.enabled";

    private @Autowired AuthUserDetailsService authUserDetailsService;
    private @Autowired AuthProperties authProperties;
    private @Autowired(required = false) DosProtectionFilter dosProtectionFilter;

    @Bean
    public DaoAuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authUserDetailsService);
        return authProvider;
    }

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth)
    {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        http
                .cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/api/**").authenticated()
                .anyRequest().permitAll()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http
                .addFilter(new JWTAuthenticationFilter(authenticationManager(), authProperties))
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), authProperties));

        if (dosProtectionFilter != null) {
            http.addFilterBefore(dosProtectionFilter, JWTAuthenticationFilter.class);
        }
    }
}
