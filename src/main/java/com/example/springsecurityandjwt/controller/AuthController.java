package com.example.springsecurityandjwt.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.springsecurityandjwt.Servise.*;
import com.example.springsecurityandjwt.DTO.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest entity) {
        log.info("Registering user with data: {}", entity);
        return ResponseEntity.ok(authService.register(entity));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest entity) {
        log.info("Authenticating user with data: {}", entity);
        return ResponseEntity.ok(authService.login(entity)); 
    }
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenRequest entity) {
        log.info("Refreshing token with data: {}", entity);
        return ResponseEntity.ok(authService.refreshToken(entity.refreshToken()));
    }
    
   @PostMapping("/logout")
   public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
    String jwt = token.substring(7);
        authService.logout(jwt);
        return ResponseEntity.ok().build();
   }
   
    
    

}
