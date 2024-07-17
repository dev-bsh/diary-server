package com.diary_server.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String email;
    private String provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String username, String email, String provider, String providerId, Role role) {
        this.username = username;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = role;
    }

    public User update(String name, String email) {
        if (name != null && email != null) {
            this.username = name;
            this.email = email;
        }
        return this;
    }

    public String getRoleKey() {
        return this.role.getKey();
    }
}
