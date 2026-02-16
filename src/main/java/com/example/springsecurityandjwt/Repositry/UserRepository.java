package com.example.springsecurityandjwt.Repositry;

import com.example.springsecurityandjwt.DTO.*;

import org.springframework.data.repository.query.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface UserRepository  extends JpaRepository<User,Long>{

   @Query("SELECT u FROM User u WHERE  u.username = :mid")
   public Optional<User> findbyUserId(@Param("mid") String mid);
   public boolean existsByUsername(String username);
     boolean existsByEmail(String email);
}
