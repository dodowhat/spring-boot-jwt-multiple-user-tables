package com.example.scaffold.repos;

import com.example.scaffold.models.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepo extends JpaRepository<AdminUser, Long> {
    AdminUser findByUsername(String username);
    AdminUser findById(long id);
}