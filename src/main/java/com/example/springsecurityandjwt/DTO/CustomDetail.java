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

    /*
    / 같은 결과를 내는 두 가지 방법:

// 방법 A (Spring Security 표준)
CustomDetail: "ROLE_ADMIN"
Security: .hasRole("ADMIN")
자동으로 role_ 접두사가 붙어서 권한이 부여됩니다.
// 방법 B (직접 제어)
CustomDetail: "ADMIN"
Security: .hasAuthority("ADMIN")
이건 자동으로 role_ 접두사가 붙지 않으며, 권한 이름을 직접 제어할 수 있습니다.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + UserInfo.getRole().name()));
    }

    @Override
    public String getPassword() {
        return UserInfo.getPassword();
    }

    @Override
    public String getUsername() {
        return UserInfo.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserInfo.isEnabled();
    }
}
