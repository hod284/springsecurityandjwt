package com.example.springsecurityandjwt.jwt;

import org.springframework.web.filter.OncePerRequestFilter;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
     final String authHeader =request.getHeader("Authorization");
     final String jwt;
     final String username;
     if(authHeader == null ||!authHeader.startsWith("Bearer ")){
        filterChain.doFilter(request, response);
        return;
     }
      try {
    jwt = authHeader.substring(7);
    username = jwtProvider.extractUsername(jwt);
    
    if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
        if (jwtProvider.isTokenValid(jwt, userDetails)) {
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );
            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
 } catch (io.jsonwebtoken.ExpiredJwtException e) {
    // 액세스 토큰 만료 - 401 응답으로 프론트엔드가 리프레시 요청하도록
    log.info("Access token expired: {}", e.getMessage());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("{\"error\":\"TOKEN_EXPIRED\",\"message\":\"Access token expired. Please refresh.\"}");
    return; // 여기서 필터 체인 중단
 } catch (Exception e) {
    log.error("JWT authentication error: {}", e.getMessage());
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write("{\"error\":\"INVALID_TOKEN\",\"message\":\"Invalid token\"}");
    return;
 }
 
 filterChain.doFilter(request, response);
    }

}
