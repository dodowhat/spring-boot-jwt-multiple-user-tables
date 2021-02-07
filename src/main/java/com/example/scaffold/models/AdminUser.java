package com.example.scaffold.models;

import com.fasterxml.jackson.annotation.JsonView;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import static com.example.scaffold.security.Constants.SECRET_LENGTH;

@Entity
@Table(name = "admin_users")
public class AdminUser {

    public interface BriefView {}
    public interface DetailedView extends BriefView {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    @Column(unique = true)
    private String username;

    @NotBlank
    private String password;

    @NotBlank
    private String jwtSecret;

    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    private Date updatedAt;

    @JsonView(BriefView.class)
    public long getId() {
        return id;
    }

    @JsonView(BriefView.class)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonView(BriefView.class)
    public String getJwtSecret() {
        return jwtSecret;
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public byte[] getJwtSecretBytes() {
        return Base64.getUrlDecoder().decode(this.jwtSecret);
    }

    public void resetJwtSecret() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[SECRET_LENGTH];
        secureRandom.nextBytes(bytes);
        this.jwtSecret = Base64.getUrlEncoder().encodeToString(bytes);
    }
}
