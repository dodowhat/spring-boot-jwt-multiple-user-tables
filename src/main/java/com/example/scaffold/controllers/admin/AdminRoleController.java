package com.example.scaffold.controllers.admin;

import com.example.scaffold.exceptions.ActionNotAllowedException;
import com.example.scaffold.models.AdminAction;
import com.example.scaffold.models.AdminRole;
import com.example.scaffold.repos.AdminActionRepo;
import com.example.scaffold.repos.AdminRoleRepo;
import com.example.scaffold.request.AdminRoleAssignActionsRequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
public class AdminRoleController extends AdminBaseController {
    private final AdminRoleRepo adminRoleRepo;
    private final AdminActionRepo adminActionRepo;

    public AdminRoleController(
            AdminRoleRepo adminRoleRepo,
            AdminActionRepo adminActionRepo
    ) {
        this.adminRoleRepo = adminRoleRepo;
        this.adminActionRepo = adminActionRepo;
    }

    @GetMapping("/admin_roles")
    @PreAuthorize("hasPermission('admin', #this.this.class.getSimpleName() + '@index')")
    public ResponseEntity<List<AdminRole>> index() {
        List<AdminRole> roles = adminRoleRepo.findAll();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/admin_roles")
    @PreAuthorize("hasPermission('admin', #this.this.class.getSimpleName() + '@create')")
    public void create(@Valid @RequestBody AdminRole adminRole) {
        adminRoleRepo.save(adminRole);
    }

    @DeleteMapping("/admin_roles/{id}")
    @PreAuthorize("hasPermission('admin', #this.this.class.getSimpleName() + '@destroy')")
    public void destroy(@PathVariable long id) throws ActionNotAllowedException {
        if (!adminRoleRepo.existsById(id)) {
            throw new EntityNotFoundException("Entity not found");
        }

        AdminRole adminRole = adminRoleRepo.findById(id);

        if (adminRole.isAdmin()) {
            throw new ActionNotAllowedException("Not allowed to delete role 'admin'");
        }

        // if (adminRoleRepo.userCount(id) > 0) {
        if (adminRole.getUsers().size() > 0) {
            throw new ActionNotAllowedException("There are users assigned to this role. Detach first");
        }

        adminRole.setUsers(new HashSet<>());
        adminRole.setActions(new HashSet<>());
        adminRoleRepo.save(adminRole);
        adminRoleRepo.delete(adminRole);
    }

    @PatchMapping("/admin_roles/{id}/assign_actions")
    @PreAuthorize("hasPermission('admin', #this.this.class.getSimpleName() + '@assignActions')")
    public void assignActions(
            @PathVariable long id,
            @RequestBody AdminRoleAssignActionsRequestBody requestBody
    ) throws ActionNotAllowedException {
        AdminRole adminRole = adminRoleRepo.findById(id);
        if (adminRole.isAdmin()) {
            throw new ActionNotAllowedException("Role 'admin' no need to assign actions");
        }

        Set<AdminAction> actions = adminActionRepo.findByIdIn(requestBody.getActionIds());
        adminRole.setActions(actions);
        adminRoleRepo.save(adminRole);
    }
}
