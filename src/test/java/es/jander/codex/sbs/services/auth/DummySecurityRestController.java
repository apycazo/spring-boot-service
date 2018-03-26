package es.jander.codex.sbs.services.auth;

import es.jander.codex.sbs.data.Duo;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
public class DummySecurityRestController
{
    public static final String PUBLIC_URI = "/public/sandbox";
    public static final String SECURE_URI = "/api/sandbox";

    @RestController
    public static class PublicAPI
    {
        @GetMapping(PUBLIC_URI)
        public Duo<String,String> getPublicStatus ()
        {
            return Duo.<String, String>builder().x("source").y("public").build();
        }
    }

    @RestController
    public static class SecureAPI
    {
        @GetMapping(SECURE_URI)
        public Duo<String,String> getPrivateStatus ()
        {
            return Duo.<String, String>builder().x("source").y("private").build();
        }
    }



}
