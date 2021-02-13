package com.example.scaffold.repos;

import com.example.scaffold.models.AdminUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.example.scaffold.models.AdminRole;

import java.util.Set;

@Repository
public interface AdminRoleRepo extends JpaRepository<AdminRole, Long> {
    AdminRole findById(long id);
    AdminRole findByName(String name);
    Set<AdminRole> findByIdIn(Set<Long> ids);
    // @Query(value = "select count(*) from admin_role_admin_user where admin_role_id = ?1", nativeQuery = true)
    // long userCount(long id);
}
