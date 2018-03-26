package es.jander.codex.sbs.services.auth;

import es.jander.codex.sbs.data.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserAuthorizationDetails implements UserDetails
{
    private UserInfo userInfo;

    public UserAuthorizationDetails (UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return Collections.emptyList();
    }

    @Override
    public String getPassword()
    {
        return userInfo.getPassword();
    }

    @Override
    public String getUsername()
    {
        return userInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired()
    {
        return true;
    }

    @Override
    public boolean isAccountNonLocked()
    {
        return userInfo.isEnabled();
    }

    @Override
    public boolean isCredentialsNonExpired()
    {
        return true;
    }

    @Override
    public boolean isEnabled()
    {
        return userInfo.isEnabled();
    }
}
