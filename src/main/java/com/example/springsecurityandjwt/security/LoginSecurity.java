package com.example.springsecurityandjwt.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Component
@EnableWebSecurity
@Configuration
public class LoginSecurity {
    
  @Bean
  public SecurityFilterChain filterChain (HttpSecurity security) throws Exception
  {
    security.csrf(csrf ->csrf.disable())
    .cors(cors -> cors.configurationSource(corsConfigurationSource()))
    .authorizeHttpRequests(auth->auth.requestMatchers("/login","/login-form").permitAll()
    .requestMatchers("/admin/**").hasAllRoles("ADMIN")
    .anyRequest().authenticated()) 
    .formLogin(login -> login.loginPage("/login-form")
    //post (/login)보내온 데이터를 가로채서 검증한다는소리  
    .loginProcessingUrl("/login")
    .defaultSuccessUrl("/home",true)
    .failureUrl("/login-form"))
    .logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login-form"));
  
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

}
