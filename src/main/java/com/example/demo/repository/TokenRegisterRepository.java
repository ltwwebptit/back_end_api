package com.example.demo.repository;

import com.example.demo.entity.TokenRegisterEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenRegisterRepository extends JpaRepository<TokenRegisterEntity, Integer> {
    TokenRegisterEntity findByToken(String token);
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN TRUE ELSE FALSE END " +
            "FROM TokenRegisterEntity t " +
            "WHERE t.user.id = :userId AND t.expirationDate > :now")
    boolean existsValidTokenByUserId(@Param("userId") Integer userId,
                                     @Param("now") java.sql.Date now);
}
