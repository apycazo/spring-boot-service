package es.jander.codex.sbs.api.httpbin;

import feign.Headers;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "http-bin-client", url = "https://httpbin.org/")
public interface HttpBinClient
{
    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @Headers("Accept: application/json")
    String sendGetRequest ();

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    @Headers("Accept: application/json") // only to show how to send headers
    String sendGetRequest (@RequestParam(value = "param") String param);

    @RequestMapping(value = "/post", method = RequestMethod.POST)
    @Headers("Content-Type: application/json")
    String sendPostRequest(@RequestBody String content);
}
