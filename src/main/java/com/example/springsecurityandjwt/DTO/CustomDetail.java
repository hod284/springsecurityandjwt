package com.example.springsecurityandjwt.DTO;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CustomDetail  implements UserDetails{
   
    public final User UserInfo;
    public CustomDetail(User Us)
    {
        UserInfo =Us;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(UserInfo.getRole()));
    }

    @Override
    public String getPassword() {
        return UserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return UserInfo.getId();
    }
}
