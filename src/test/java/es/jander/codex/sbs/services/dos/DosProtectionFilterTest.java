package es.jander.codex.sbs.services.dos;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "app.dos.enabled=true",
                "app.dos.bucketCapacity=5",
                "app.dos.tokenSpawnTimeInMillis=2000",
                "app.dos.paths[0]=/login"
        }
)
public class DosProtectionFilterTest
{

    @Value("${app.dos.enabled}")
    private boolean isDosProtectionEnabled;
    @Value("${app.dos.tokenSpawnTimeInMillis}")
    private long spawnTime;

    private static final String username = "test-user";
    private static final String name = "test-user-name";
    private static final String credentials = "test-user-password";

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private class LoginCredentials
    {
        private String username;
        private String password;
    }

    private @LocalServerPort int port;
    private @Autowired DosProperties dosProperties;
    private @Autowired UserInfoRepository userInfoRepository;
    private @Autowired TestRestTemplate testRestTemplate;

    private ResponseEntity<String> loginRequest (String username, String password)
    {
        URI uri = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("localhost")
                .port(port)
                .path("/login")
                .build().toUri();

        LoginCredentials loginCredentials = new LoginCredentials(username, password);

        return testRestTemplate.exchange(
                RequestEntity.post(uri).contentType(MediaType.APPLICATION_JSON).body(loginCredentials),
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
    public void test_properties_are_correct ()
    {
        assertThat(dosProperties.getBucketCapacity()).isEqualTo(5);
        assertThat(isDosProtectionEnabled).isTrue();
        assertThat(spawnTime).isEqualTo(2000L);
        assertThat(dosProperties.getPaths()).isNotEmpty();
        assertThat(dosProperties.getPaths().size()).isEqualTo(1);
    }

    @Test
    public void test_request_rate_is_limited () throws InterruptedException
    {
        List<ResponseEntity<String>> responses = new ArrayList<>(10);
        IntStream.range(0,10).forEach(i -> responses.add(loginRequest(username, credentials)));

        long successCount = responses.stream().filter(r -> r.getStatusCode().is2xxSuccessful()).count();
        assertThat(successCount).isEqualTo(5);

        long rejectedCount = responses.stream().filter(r -> r.getStatusCode().equals(HttpStatus.TOO_MANY_REQUESTS)).count();
        assertThat(rejectedCount).isEqualTo(5);

        Thread.sleep(2100);

        ResponseEntity<String> response = loginRequest(username, credentials);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

}