package com.example.scaffold.controllers.admin;

import com.example.scaffold.repos.AdminUserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.scaffold.models.AdminUser;

@RestController
public class AdminUserController extends AdminBaseController {

	private final AdminUserRepo adminUserRepo;
	private final PasswordEncoder passwordEncoder;
	
	public AdminUserController(AdminUserRepo adminUserRepo, PasswordEncoder passwordEncoder) {
		this.adminUserRepo = adminUserRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostMapping("/admin_users")
	public void create(@RequestBody AdminUser adminUser) {
		adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
		adminUser.resetJwtSecret();

		adminUserRepo.save(adminUser);
	}

	@GetMapping("/admin_users/init")
	public void init() {
		AdminUser adminUser = new AdminUser();

		adminUser.setUsername("admin");
		adminUser.setPassword(passwordEncoder.encode("admin"));
		adminUser.resetJwtSecret();

		adminUserRepo.save(adminUser);
	}

}
