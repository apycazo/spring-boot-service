package es.jander.codex.sbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@EnableFeignClients
@SpringBootApplication
public class App extends WebMvcConfigurerAdapter
{
    public static void main (String [] args)
    {
        SpringApplication.run(App.class, args);
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer config)
    {
        config
                // path extensions will always be used when present
                .favorPathExtension(true)
                // allow us to define media types manually
                .useJaf(false)
                // Note that a browser might send a default xml header, which would override this
                .defaultContentType(MediaType.APPLICATION_JSON)
                // Extensions we allow to use (note that using xml requires dependency on 'jackson-dataformat-xml')
                .mediaType("xml", MediaType.APPLICATION_XML)
                .mediaType("json", MediaType.APPLICATION_JSON);
    }
}
