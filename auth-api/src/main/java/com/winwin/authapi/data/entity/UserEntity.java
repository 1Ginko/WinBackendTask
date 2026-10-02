package com.winwin.authapi.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = UserEntity.USER_TABLE)
public class UserEntity {

    static final String USER_TABLE = "users";
    static final String USER_ID = "id";
    static final String USER_EMAIL = "email";
    static final String USER_PASSWORD_HASH = "password_hash";

    @Id
    @Column(name = USER_ID, nullable = false, updatable = false)
    private UUID id;

    @Column(name = USER_EMAIL, nullable = false, length = 320)
    private String email;

    @Column(name = USER_PASSWORD_HASH, nullable = false, length = 255)
    private String passwordHash;

    protected UserEntity() {}

    public UserEntity(UUID id, String email, String passwordHash) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }
}