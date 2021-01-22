package com.example.scaffold.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class AppUserDetails extends User {
    public Long id;
    public String jwtSecret;

    public AppUserDetails(Long id,
                          String jwtSecret,
                          String username,
                          String password,
                          Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.id = id;
        this.jwtSecret = jwtSecret;
    }

    public Long getId() {
        return id;
    }

    public String getJwtSecret() {
        return jwtSecret;
    }
}
