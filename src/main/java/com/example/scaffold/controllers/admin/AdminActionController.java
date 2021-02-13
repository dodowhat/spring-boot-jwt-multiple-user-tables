package com.example.scaffold.controllers.admin;

import com.example.scaffold.models.AdminActionGroup;
import com.example.scaffold.repos.AdminActionGroupRepo;
import com.example.scaffold.repos.AdminActionRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AdminActionController extends AdminBaseController {
    private final AdminActionGroupRepo adminActionGroupRepo;
    private final AdminActionRepo adminActionRepo;

    public AdminActionController(
            AdminActionGroupRepo adminActionGroupRepo,
            AdminActionRepo adminActionRepo
    ) {
        this.adminActionGroupRepo = adminActionGroupRepo;
        this.adminActionRepo = adminActionRepo;
    }

    @GetMapping("/admin_actions")
    @PreAuthorize("hasPermission('admin', #this.this.class.getSimpleName() + '@index')")
    public ResponseEntity<List<AdminActionGroup>> index() {
        List<AdminActionGroup> adminActionGroups = adminActionGroupRepo.findAll();
        return ResponseEntity.ok(adminActionGroups);
    }
}
