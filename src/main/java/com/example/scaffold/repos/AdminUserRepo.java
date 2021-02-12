package com.example.scaffold.repos;

import com.example.scaffold.models.AdminUser;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminUserRepo extends PagingAndSortingRepository<AdminUser, Long> {
    AdminUser findByUsername(String username);
    AdminUser findById(long id);
}