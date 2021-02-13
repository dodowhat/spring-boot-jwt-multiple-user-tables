package com.example.scaffold.models;

import com.example.scaffold.serializers.AdminUserSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

import static com.example.scaffold.security.Constants.SECRET_LENGTH;

@Entity
@Table(name = "admin_users")
@JsonSerialize(using = AdminUserSerializer.class)
public class AdminUser {

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

    @ManyToMany
    @JoinTable(
            name = "admin_role_admin_user",
            joinColumns = @JoinColumn(name = "admin_user_id"),
            inverseJoinColumns = @JoinColumn(name = "admin_role_id")
    )
    private Set<AdminRole> roles;

    public long getId() {
        return id;
    }

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

    public String getJwtSecret() {
        return jwtSecret;
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

    public Set<AdminRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<AdminRole> roles) {
        this.roles = roles;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public boolean isAdmin() {
        for (AdminRole role : getRoles()) {
            if (role.isAdmin()) {
                return true;
            }
        }
        return false;
    }
}
