package com.example.springsecurityandjwt.DTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Entity
@Table(name = "Users")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User  {
       
    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    private long index;

    @Column(unique =  true, nullable = false)
    private String id;

    @Column(nullable = false)
    private String password;
   
    @Column(nullable = false)
    private String role;
}
