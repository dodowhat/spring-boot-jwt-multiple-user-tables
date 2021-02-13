package com.example.scaffold.security;

import com.example.scaffold.models.AdminAction;
import com.example.scaffold.models.AdminRole;
import com.example.scaffold.models.AdminUser;
import com.example.scaffold.repos.AdminActionRepo;
import com.example.scaffold.repos.AdminUserRepo;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Configuration
public class RBACPermissionEvaluator implements PermissionEvaluator {

    private final AdminUserRepo adminUserRepo;
    private final AdminActionRepo adminActionRepo;
    public RBACPermissionEvaluator(
            AdminUserRepo adminUserRepo,
            AdminActionRepo adminActionRepo
    ) {
        this.adminUserRepo = adminUserRepo;
        this.adminActionRepo = adminActionRepo;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Object domain, Object action) {
        if ("admin".equals((String) domain)) {
            AdminUser adminUser = adminUserRepo.findByUsername(authentication.getName());
            if (adminUser.isAdmin()) {
                return true;
            }
            AdminAction adminAction = adminActionRepo.findByAction((String) action);
            Set intersectionRoles = new HashSet<AdminRole>(adminUser.getRoles());
            intersectionRoles.retainAll(adminAction.getRoles());
            return intersectionRoles.size() > 0;
        }
        return false;
    }

    @Override
    public boolean hasPermission(
            Authentication authentication,
            Serializable targetId,
            String targetType,
            Object permission
    ) {
        return false;
    }
}
