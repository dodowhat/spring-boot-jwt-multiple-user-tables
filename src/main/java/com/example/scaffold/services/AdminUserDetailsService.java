package com.example.scaffold.services;

import com.example.scaffold.models.AdminUser;
import com.example.scaffold.repos.AdminUserRepo;
import com.example.scaffold.security.AdminUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    private final AdminUserRepo adminUserRepo;

    public AdminUserDetailsService(AdminUserRepo adminUserRepo) {
        this.adminUserRepo = adminUserRepo;
    }

    @Override
    public AdminUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AdminUser adminUser = adminUserRepo.findByUsername(username);
        if (adminUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AdminUserDetails(adminUser.getId(),
                adminUser.getJwtSecret(),
                adminUser.getUsername(),
                adminUser.getPassword(),
                new ArrayList<>()
        );
    }
}
