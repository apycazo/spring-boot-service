package es.jander.codex.sbs.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.jander.codex.sbs.data.Duo;
import es.jander.codex.sbs.data.UserInfo;
import es.jander.codex.sbs.persistence.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@Slf4j
@RestController
public class SandboxService
{
    private @Autowired ObjectMapper objectMapper;
    private @Autowired UserInfoRepository userInfoRepository;
    private @Autowired AuthenticationManager authManager;

    @PostConstruct
    private void afterBuild ()
    {
        log.info("Build successful");
        UserInfo userInfo = UserInfo.builder()
                .username("service-admin")
                .name("the admin")
                .password("1234")
                .build();
        userInfoRepository.save(userInfo);
    }

//    @GetMapping("/login")
//    public void login ()
//    {
//
//    }

    @GetMapping("public/sandbox")
    public Duo<String,String> getPublicStatus ()
    {
        return Duo.<String, String>builder().x("source").y("public").build();
    }

    @GetMapping("api/sandbox")
    public Duo<String,String> getPrivateStatus ()
    {
        return Duo.<String, String>builder().x("source").y("private").build();
    }


}
