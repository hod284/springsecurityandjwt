package com.example.springsecurityandjwt.DTO;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
   // enum 값을 string으로 db에 저장
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

   @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    // 여기서 protected jpa에서 dbupdate나 insert할때만 쓰겠다는뜻
    //prepersist  insert직전에 실행
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
        //preupdate  update직전에 실행
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
