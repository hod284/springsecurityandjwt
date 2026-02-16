package com.example.springsecurityandjwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import lombok.RequiredArgsConstructor;
import com.example.springsecurityandjwt.jwt.JwtAuthenticationFilter;
import java.util.Arrays;

@Component
@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class LoginSecurity {
        
  private final JwtAuthenticationFilter jwtAuthFilter;
  @Bean
  public SecurityFilterChain filterChain (HttpSecurity security) throws Exception
  {
    security.csrf(csrf ->csrf.disable())
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .authorizeHttpRequests(auth->auth
    .requestMatchers("/api/auth/**","/h2-console/**").permitAll()
    // websocket
    .requestMatchers("/ws-monitoring/**").permitAll()
    /*
    📊 Actuator & Prometheus 설명
     간단하게 말하면:
      Actuator: Spring Boot 앱의 건강 상태와 메트릭을 HTTP로 보여주는 도구
       Prometheus: 메트릭을 수집하고 저장하는 모니터링 시스템
     */
    // Actuator & Prometheus
    .requestMatchers("/actuator/**").permitAll()
    .requestMatchers("/api/monitoring/**").hasRole("ADMIN")
    .anyRequest().authenticated()).
    sessionManagement(session ->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
    .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
    /* 
    만약 jwt랑 redis를 안사용한다면 이걸 사용해도 됨 하지만 jwt랑 redis를 사용하기 때문에 주석처리
    .formLogin(login -> login.loginPage("/login-form")
    //post (/login)보내온 데이터를 가로채서 검증한다는소리  
    .loginProcessingUrl("/login")
    .defaultSuccessUrl("/home",true)
    .failureUrl("/login-form"))
    .logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login-form"));
      */
    return security.build();
  } 
   @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
     //“로그인 요청이 진짜인지 아닌지 최종 판결 내리는 놈”
     @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
   @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
}
