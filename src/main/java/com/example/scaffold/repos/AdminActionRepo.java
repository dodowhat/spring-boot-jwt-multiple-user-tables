package com.example.scaffold.repos;

import com.example.scaffold.models.AdminAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface AdminActionRepo extends JpaRepository<AdminAction, Long> {
    Set<AdminAction> findByIdIn(Set<Long> ids);
    AdminAction findByAction(String action);
}
