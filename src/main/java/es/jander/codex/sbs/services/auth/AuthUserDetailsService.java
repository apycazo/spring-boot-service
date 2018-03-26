package es.jander.codex.sbs.services.auth;

import es.jander.codex.sbs.data.UserInfo;
import es.jander.codex.sbs.persistence.UserInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthUserDetailsService implements UserDetailsService
{
    private @Autowired UserInfoRepository userInfoRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        UserInfo userRecord = userInfoRepository.findByUsername(username);
        if (userRecord == null) {
            throw new UsernameNotFoundException(username);
        }

        return new UserAuthorizationDetails(userRecord);
    }
}
