package com.example.springsecurityandjwt.Repositry;

import com.example.springsecurityandjwt.DTO.*;

import io.lettuce.core.dynamic.annotation.Param;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;

public interface UserRepositry  extends JpaRepository<User,Long>{

   @Query("SELECT u FROM Users u WHERE u.id = : mid")
   public Optional<User> findbyUserId(@Param("mid") String mid);
}
