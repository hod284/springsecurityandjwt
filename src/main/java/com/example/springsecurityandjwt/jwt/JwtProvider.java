package com.example.springsecurityandjwt.jwt;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

@Component
public class JwtProvider {

   @Value("${jwt.secrect}") 
   private String Secret;

   @Value("${jwt.AcessExpire}")
   private Long AcessExpire;

    @Value("${jwt.RefreshExpire}")
   private Long RefreshExpire;

  

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
   public Claims extractAllClaims(String token) {
         return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(Secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
     public String GeneratedAcessToken( UserDetails userDetails)
   {
         return  MaketheToken(new HashMap<>(), userDetails, AcessExpire);
   }
   public String GeneratedAcessToken(Map<String, Object> extraClaims, UserDetails userDetails)
   {
         return  MaketheToken(extraClaims, userDetails, AcessExpire);
   }
    public String GeneratedRefreshToken( UserDetails userDetails)
   {
       return  MaketheToken(new HashMap<>(), userDetails, RefreshExpire);
   }
   private String MaketheToken(
     Map<String, Object> extraClaims,
            UserDetails userDetails,
    Long expireday)
   {
       
      return Jwts.builder()
        .claims(extraClaims)
              .subject(userDetails.getUsername())
              .issuedAt(new Date(System.currentTimeMillis()))
              .expiration(new Date(System.currentTimeMillis() + expireday))
              .signWith(getSignInKey())
              .compact();
   }
   public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    public boolean isTokenExpired(String token) {
        // 이시간 보다 과거냐는 before을 쓰면 된다
        return extractExpiration(token).before(new Date());
    }
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
      public long getExpirationTime(String token) {
        return extractExpiration(token).getTime();
    }
}
