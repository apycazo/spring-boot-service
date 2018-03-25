package es.jander.codex.sbs.service.auth;

import es.jander.codex.sbs.data.Duo;
import es.jander.codex.sbs.data.UserInfo;
import es.jander.codex.sbs.persistence.UserInfoRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@Import(DummySecurityRestController.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTSecurityConfigTest
{
    private @Autowired UserInfoRepository userInfoRepository;
    private @Autowired TestRestTemplate testRestTemplate;
    private @Autowired AuthProperties authProperties;

    private @LocalServerPort Integer port;

    private static final String username = "test-user";
    private static final String name = "test-user-name";
    private static final String credentials = "test-user-password";
    private static final String loginURI = "/login";
    private static final String logoutURI = "/logout";
    private static final String host = "localhost";
    private static final String scheme = "http";

    private ParameterizedTypeReference<Duo<String, String>> typeReference;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class LoginCredentials
    {
        private String username;
        private String password;
    }

    public JWTSecurityConfigTest ()
    {
        typeReference = new ParameterizedTypeReference<Duo<String, String>>() {};
    }

    private ResponseEntity<String> loginRequest (String username, String password)
    {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(loginURI)
                .build().toUri();

        LoginCredentials loginCredentials = new LoginCredentials(username, password);

        return testRestTemplate.exchange(
                RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(loginCredentials),
                String.class
        );
    }

    private ResponseEntity<String> logoutRequest (String jwtToken)
    {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(logoutURI)
                .build().toUri();

        return testRestTemplate.exchange(
                RequestEntity.post(uri).header(authProperties.getHeaderString(), jwtToken).build(),
                String.class
        );
    }

    @Before
    public void createSampleUser ()
    {
        userInfoRepository.deleteAll();
        UserInfo userInfo = UserInfo.builder()
                .username(username)
                .name(name)
                .password(credentials)
                .build();

        userInfoRepository.save(userInfo);
    }

    @Test
    public void test_access_public_api ()
    {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(DummySecurityRestController.PUBLIC_URI)
                .build().toUri();

        ResponseEntity<Duo<String,String>> response = testRestTemplate.exchange(
                RequestEntity.get(uri).build(),
                typeReference
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getY()).isEqualTo("public");
    }

    @Test
    public void test_access_secured_api_after_login ()
    {
        ResponseEntity<String> loginResponse = loginRequest(username, credentials);
        String jwtToken = loginResponse.getHeaders().get(authProperties.getHeaderString()).get(0);

        URI uri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(DummySecurityRestController.SECURE_URI)
                .build().toUri();

        ResponseEntity<Duo<String,String>> response = testRestTemplate.exchange(
                RequestEntity.get(uri).header(authProperties.getHeaderString(), jwtToken).build(),
                typeReference
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getY()).isEqualTo("private");
    }

    @Test
    public void test_access_without_credential_on_secured_api_is_forbidden ()
    {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme(scheme)
                .host(host)
                .port(port)
                .path(DummySecurityRestController.SECURE_URI)
                .build().toUri();


        ResponseEntity<String> response = testRestTemplate.exchange(
                RequestEntity.get(uri).build(),
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        List<String> authenticateHeaderValues = response.getHeaders().get(HttpHeaders.WWW_AUTHENTICATE);
        assertThat(authenticateHeaderValues).isNotNull();
        assertThat(authenticateHeaderValues).isNotEmpty();
        assertThat(authenticateHeaderValues.size()).isEqualTo(1);
        assertThat(authenticateHeaderValues.get(0)).isEqualTo(authProperties.getAuthenticationEndpoint());
    }

    @Test
    public void test_login_request ()
    {
        ResponseEntity<String> response = loginRequest(username, credentials);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> authenticateHeaderValues = response.getHeaders().get(authProperties.getHeaderString());
        assertThat(authenticateHeaderValues).isNotNull();
        assertThat(authenticateHeaderValues.size()).isEqualTo(1);

        String authorization = authenticateHeaderValues.get(0);
        assertThat(authorization).startsWith(authProperties.getTokenPrefix());
    }

    @Test
    public void test_login_request_with_bad_credentials ()
    {
        ResponseEntity<String> response = loginRequest(username, "bad-password");
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void test_logout ()
    {
        ResponseEntity<String> loginResponse = loginRequest(username, credentials);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<String> authenticateHeaderValues = loginResponse.getHeaders().get(authProperties.getHeaderString());
        String jwtToken = authenticateHeaderValues.get(0);

        ResponseEntity<String> logoutResponse = logoutRequest(jwtToken);
        assertThat(logoutResponse.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    }
}