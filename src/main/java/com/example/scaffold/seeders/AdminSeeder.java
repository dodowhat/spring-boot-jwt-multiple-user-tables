package com.example.scaffold.seeders;

import com.example.scaffold.models.AdminActionGroup;
import com.example.scaffold.models.AdminRole;
import com.example.scaffold.models.AdminUser;
import com.example.scaffold.repos.AdminActionGroupRepo;
import com.example.scaffold.repos.AdminActionRepo;
import com.example.scaffold.repos.AdminRoleRepo;
import com.example.scaffold.repos.AdminUserRepo;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

//@Component
public class AdminSeeder {

    private final AdminActionGroupRepo adminActionGroupRepo;
    private final AdminActionRepo adminActionRepo;
    private final AdminRoleRepo adminRoleRepo;
    private final AdminUserRepo adminUserRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminSeeder(
            AdminActionGroupRepo adminActionGroupRepo,
            AdminActionRepo adminActionRepo,
            AdminRoleRepo adminRoleRepo,
            AdminUserRepo adminUserRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.adminActionGroupRepo = adminActionGroupRepo;
        this.adminActionRepo = adminActionRepo;
        this.adminRoleRepo = adminRoleRepo;
        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

//    @EventListener
    public void run(ContextRefreshedEvent event) {
        AdminRole adminRole = new AdminRole("admin");
        adminRoleRepo.save(adminRole);

        Set<AdminRole> roles = new HashSet<>();
        roles.add(adminRole);

        AdminUser adminUser = new AdminUser();
        adminUser.setUsername("admin");
        adminUser.setPassword(passwordEncoder.encode("admin"));
        adminUser.resetJwtSecret();
        adminUser.setRoles(roles);
        adminUserRepo.save(adminUser);

        roles.clear();

        adminRole = new AdminRole("editor");
        adminRoleRepo.save(adminRole);

        roles.add(adminRole);

        adminUser = new AdminUser();
        adminUser.setUsername("editor");
        adminUser.setPassword(passwordEncoder.encode("editor"));
        adminUser.resetJwtSecret();
        adminUser.setRoles(roles);
        adminUserRepo.save(adminUser);
    }
}
