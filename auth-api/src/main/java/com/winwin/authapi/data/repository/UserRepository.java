package com.winwin.authapi.data.repository;

import com.winwin.authapi.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    @Query(
            value = "SELECT * FROM users WHERE email = :email",
            nativeQuery = true
    )
    Optional<UserEntity> findByEmail(@Param("email") String email);

    @Query(
            value = "SELECT EXISTS ( SELECT 1 FROM users WHERE email = :email)",
            nativeQuery = true
    )
    boolean existsByEmail(@Param("email") String email);
}