package com.example.springsecurityandjwt.Servise;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.example.springsecurityandjwt.DTO.CustomDetail;
import com.example.springsecurityandjwt.DTO.User;
import com.example.springsecurityandjwt.Repositry.UserRepository;
@Service
// final이나 notnull이 있을때 자동으로 생성자 집어 넣어서 인스턴트 생성해주는 거임
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    public final UserRepository userRepository;

    

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException { 
       User us = userRepository.findbyUserId(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
                return new CustomDetail(us);
    }

}
