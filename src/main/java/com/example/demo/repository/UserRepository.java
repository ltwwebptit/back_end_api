package com.example.demo.repository;

import com.example.demo.entity.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, Integer> {
    Optional<UsersEntity> findByUsernameAndStatus(String username,Integer status);
    Optional<UsersEntity> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<UsersEntity> findByRolename(String roleName);
}
