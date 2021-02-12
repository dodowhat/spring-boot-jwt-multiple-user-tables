package com.example.scaffold.repos;

import com.example.scaffold.models.AdminActionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminActionGroupRepo extends JpaRepository<AdminActionGroup, Long> {
}
