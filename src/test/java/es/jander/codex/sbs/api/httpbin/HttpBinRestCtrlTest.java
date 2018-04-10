package es.jander.codex.sbs.api.httpbin;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HttpBinRestCtrlTest
{
    @Autowired
    private TestRestTemplate testRestTemplate;

    @MockBean
    private HttpBinClient client;

    private String baseContent = "{\n" +
            "    \"args\": {},\n" +
            "    \"headers\": {\n" +
            "        \"Accept\": \"*/*\",\n" +
            "        \"Connection\": \"close\",\n" +
            "        \"Host\": \"httpbin.org\",\n" +
            "        \"User-Agent\": \"Java/1.8.0_162\"\n" +
            "    },\n" +
            "    \"origin\": \"127.0.0.1\",\n" +
            "    \"url\": \"https://httpbin.org/get\",\n" +
            "    \"source\": \"mockito\"\n" +
            "}";

    @Test
    public void sendGetRequest()
    {
        // mock the response
        when(client.sendGetRequest()).thenReturn(baseContent);

        ResponseEntity response = testRestTemplate.getForEntity(HttpBinRestCtrl.BASE_PATH, String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().toString()).isEqualTo(baseContent);
    }
}