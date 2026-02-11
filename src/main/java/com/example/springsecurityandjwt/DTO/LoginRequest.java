package com.example.springsecurityandjwt.DTO;

public record LoginRequest(
    String username, 
    String password) {
    
}
