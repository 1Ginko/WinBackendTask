package com.winwin.authapi.data.repository;

import com.winwin.authapi.data.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query(
            value = """
                INSERT INTO users (id, email, password_hash)
                VALUES (:id, :email, :passwordHash)
                ON CONFLICT (email) DO NOTHING
                """,
            nativeQuery = true
    )
    int insertUser(
            @Param("id") UUID id,
            @Param("email") String email,
            @Param("passwordHash") String passwordHash
    );
}
