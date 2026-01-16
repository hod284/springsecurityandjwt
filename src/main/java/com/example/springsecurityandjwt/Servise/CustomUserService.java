package com.example.springsecurityandjwt.Servise;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.springsecurityandjwt.DTO.CustomDetail;
import com.example.springsecurityandjwt.DTO.User;
import com.example.springsecurityandjwt.Repositry.UserRepositry;
@Service
public class CustomUserService implements UserDetailsService {

    public final UserRepositry repositry;
    public CustomUserService( UserRepositry repo)
    {
            repositry =repo;
    }
    @Override
    public UserDetails loadUserByUsername(String username) {
      User user = repositry.findbyUserId(username);
      if(user == null)
      throw new UsernameNotFoundException("유저 없음");         
        return new CustomDetail(user);
    }
    

    
}
