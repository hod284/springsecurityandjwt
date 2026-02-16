package com.example.springsecurityandjwt.Servise;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import com.example.springsecurityandjwt.Repositry.UserRepository;
import com.example.springsecurityandjwt.jwt.JwtProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.springsecurityandjwt.DTO.*;
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final AuthenticationManager authenticationManager;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
   public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new RuntimeException("Username is already taken");
        }
         if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("이미 존재하는 이메일입니다");
        }
        User user = User.builder()
                .username(request.username())
                .password(passwordEncoder.encode(request.password()))
                .email(request.email())
                .role(Role.USER)
                .enabled(true)
                .build();
         
        userRepository.save(user);
        String accessToken = jwtProvider.GeneratedAcessToken(new CustomDetail(user));
        String refreshToken = jwtProvider.GeneratedRefreshToken(new CustomDetail(user));
      // Refresh Token을 Redis에 저장
        redisTemplate.opsForValue()
                .set("REFRESH:" + user.getUsername(), refreshToken, jwtProvider.RefreshExpire, TimeUnit.DAYS);
         log.info("User registered in: {}", user.getUsername());
         return new AuthResponse(accessToken,
            refreshToken,
            "Bearer",jwtProvider.AcessExpire,
            user.getUsername(),user.getRole().name());
    }
    @Transactional(readOnly = true)
     public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        User user = userRepository.findbyUserId(request.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String accessToken = jwtProvider.GeneratedAcessToken(new CustomDetail(user));
        String refreshToken = jwtProvider.GeneratedRefreshToken(new CustomDetail(user));
           // Refresh Token을 Redis에 저장
        redisTemplate.opsForValue()
                .set("REFRESH:" + user.getUsername(), refreshToken, jwtProvider.RefreshExpire, TimeUnit.MICROSECONDS);
         log.info("User logged in: {}", user.getUsername());
          return new AuthResponse(accessToken,
            refreshToken,
            "Bearer",jwtProvider.AcessExpire,
            user.getUsername(),user.getRole().name());
    }
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtProvider.extractUsername(refreshToken);
        String storedRefreshToken = redisTemplate.opsForValue().get("REFRESH:" + username);

        if (storedRefreshToken == null ) 
            throw new RuntimeException("Refresh token이 만료되었거나 존재하지 않습니다");
           
        if (!storedRefreshToken.equals(refreshToken)) 
            throw new RuntimeException("유효하지 않은 Refresh token입니다");

        User user = userRepository.findbyUserId(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!jwtProvider.isTokenValid(storedRefreshToken, new CustomDetail(user)))
            throw new RuntimeException("만료된 Refresh token입니다");

        String newAccessToken = jwtProvider.GeneratedAcessToken(new CustomDetail(user));

        log.info("Refresh token issued for user: {}", user.getUsername());

      
         return new AuthResponse(newAccessToken,
            refreshToken,
            "Bearer",jwtProvider.AcessExpire,
            user.getUsername(),user.getRole().name());
    }
     public void logout(String accessToken) {
        String username = jwtProvider.extractUsername(accessToken);
        redisTemplate.delete("REFRESH:" + username);
        log.info("User logged out: {}", username);
     }
}