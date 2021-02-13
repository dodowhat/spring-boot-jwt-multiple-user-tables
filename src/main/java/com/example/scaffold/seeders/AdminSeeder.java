package com.example.scaffold.seeders;

import com.example.scaffold.models.AdminAction;
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
        AdminRole role = new AdminRole("admin");
        adminRoleRepo.save(role);

        Set<AdminRole> roles = new HashSet<>();
        roles.add(role);

        AdminUser user = new AdminUser();
        user.setUsername("admin");
        user.setPassword(passwordEncoder.encode("admin"));
        user.resetJwtSecret();
        user.setRoles(roles);
        adminUserRepo.save(user);

        roles.clear();

        role = new AdminRole("editor");
        adminRoleRepo.save(role);

        roles.add(role);

        user = new AdminUser();
        user.setUsername("editor");
        user.setPassword(passwordEncoder.encode("editor"));
        user.resetJwtSecret();
        user.setRoles(roles);
        adminUserRepo.save(user);

        AdminActionGroup group = new AdminActionGroup("AdminUsers");
        adminActionGroupRepo.save(group);
        AdminAction action = new AdminAction("AdminUserController@index", "List AdminUser");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminUserController@create", "Create AdminUser");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminUserController@show", "Show AdminUser");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminUserController@destroy", "Delete AdminUser");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminUserController@assignRoles", "Assign AdminUser Roles");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminUserController@resetPassword", "Reset AdminUser Password");
        action.setGroup(group);
        adminActionRepo.save(action);

        group = new AdminActionGroup("AdminRoles");
        adminActionGroupRepo.save(group);
        action = new AdminAction("AdminRoleController@index", "List AdminRoles");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminRoleController@create", "Create AdminRole");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminRoleController@destroy", "Delete AdminRole");
        action.setGroup(group);
        adminActionRepo.save(action);
        action = new AdminAction("AdminRoleController@assignActions", "Assign AdminRole Actions");
        action.setGroup(group);
        adminActionRepo.save(action);

        group = new AdminActionGroup("AdminActions");
        adminActionGroupRepo.save(group);
        action = new AdminAction("AdminActionController@index", "List AdminActions");
        action.setGroup(group);
        adminActionRepo.save(action);
    }
}
