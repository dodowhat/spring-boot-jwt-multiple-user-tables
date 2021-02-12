package com.example.scaffold.controllers.admin;

import com.example.scaffold.exceptions.ActionNotAllowedException;
import com.example.scaffold.models.AdminAction;
import com.example.scaffold.models.AdminRole;
import com.example.scaffold.repos.AdminActionRepo;
import com.example.scaffold.repos.AdminRoleRepo;
import com.example.scaffold.request.AdminRoleAssignActionsRequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
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
    public ResponseEntity<List<AdminRole>> index() {
        List<AdminRole> roles = adminRoleRepo.findAll();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/admin_roles")
    public void create(@RequestBody AdminRole adminRole) {
        adminRoleRepo.save(adminRole);
    }

    @DeleteMapping("/admin_roles/{id}")
    public void destroy(@PathVariable long id) throws ActionNotAllowedException {
        if (!adminRoleRepo.existsById(id)) {
            throw new EntityNotFoundException("Entity not found");
        }

        AdminRole adminRole = adminRoleRepo.findById(id);

        if (adminRole.getName() == "admin") {
            throw new ActionNotAllowedException("Not allowed to delete role 'admin'");
        }

//        if (adminRoleRepo.userCount(id) > 0) {
        if (adminRole.getUsers().size() > 0) {
            throw new ActionNotAllowedException("There are users assigned to this role. Detach first");
        }

        adminRole.setUsers(new HashSet<>());
        adminRole.setActions(new HashSet<>());
        adminRoleRepo.save(adminRole);
        adminRoleRepo.delete(adminRole);
    }

    @PatchMapping("/admin_roles/{id}/assign_actions")
    public void assignActions(
            @PathVariable long id,
            @RequestBody AdminRoleAssignActionsRequestBody requestBody
    ) throws ActionNotAllowedException {
        AdminRole adminRole = adminRoleRepo.findById(id);
        if (adminRole.getName() == "admin") {
            throw new ActionNotAllowedException("Role 'admin' no need to assign actions");
        }

        Set<AdminAction> actions = adminActionRepo.findByIdIn(requestBody.getActionIds());
        adminRole.setActions(actions);
        adminRoleRepo.save(adminRole);
    }
}
