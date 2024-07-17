package com.diary_server.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;
    private Long userId;
    private Date expiryDate;

    @Builder
    public RefreshToken(String token, Long userId, Date expiryDate) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = expiryDate;
    }

    public RefreshToken update(String token, Long userId, Date expiryDate) {
        if (token != null && userId != null && expiryDate != null) {
            this.token = token;
            this.userId = userId;
            this.expiryDate = expiryDate;
        }
        return this;
    }
}
