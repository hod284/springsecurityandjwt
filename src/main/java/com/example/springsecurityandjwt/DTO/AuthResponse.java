package com.example.springsecurityandjwt.DTO;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    String tokenType,
    Long expiresIn,
    String username,
    String role
) {

}
