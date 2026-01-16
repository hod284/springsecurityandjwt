package com.example.springsecurityandjwt.jwt;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtProvider {
    
    private final SecretKey key;

   @Value("${jwt.secrect}") 
   private String Secret;

   @Value("${jwt.AcessExpire}")
   private Long AcessExpire;

    @Value("${jwt.RefreshExpire}")
   private Long RefreshExpire;

   public JwtProvider()
   {
        byte[] keybyte = Decoders.BASE64.decode(java.util.Base64.getEncoder().encodeToString(Secret.getBytes()));
        key =Keys.hmacShaKeyFor(keybyte);
   }
   
   public String GeneratedAcessToken()
   {
         return  MaketheToken(AcessExpire);
   }
    public String GeneratedRefreshToken()
   {
       return  MaketheToken(RefreshExpire);
   }
   private String MaketheToken(Long expireday)
   {
       Authentication au = SecurityContextHolder.getContext().getAuthentication();
       UserDetails details = (UserDetails)au.getPrincipal();
       Date now = new Date();
       Date expire = new Date(now.getTime() +expireday);
      return Jwts.builder()
              .subject(details.getUsername())
              .issuedAt(now)
              .expiration(expire)
              .signWith(key)
              .compact();
   }

}
