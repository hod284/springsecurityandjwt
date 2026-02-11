package com.example.springsecurityandjwt.DTO;

public record RegisterRequest( 
    String username,
    String password,
    String email) {
    
}
