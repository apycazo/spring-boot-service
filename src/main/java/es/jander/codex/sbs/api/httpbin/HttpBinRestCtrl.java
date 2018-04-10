package es.jander.codex.sbs.api.httpbin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(HttpBinRestCtrl.BASE_PATH)
public class HttpBinRestCtrl
{
    public static final String BASE_PATH = "/httpbin";

    @Autowired
    private HttpBinClient client;

    @GetMapping
    public String sendGetRequest()
    {
        return client.sendGetRequest();
    }
}
