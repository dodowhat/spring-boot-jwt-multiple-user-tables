package com.example.scaffold.controllers.admin;

import com.example.scaffold.repos.AdminUserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.scaffold.models.AdminUser;

@RestController
public class AdminUsersController extends AdminBaseController {

	private final AdminUserRepo adminUserRepo;
	private final PasswordEncoder passwordEncoder;
	
	public AdminUsersController(AdminUserRepo adminUserRepo, PasswordEncoder passwordEncoder) {
		this.adminUserRepo = adminUserRepo;
		this.passwordEncoder = passwordEncoder;
	}
	
	@PostMapping("/users")
	public void create(@RequestBody AdminUser adminUser) {
		adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
		adminUser.setJwtSecret(passwordEncoder.encode(adminUser.getUsername()));

		adminUserRepo.save(adminUser);
	}

	@PostMapping("/users/init")
	public void init() {
		AdminUser adminUser = new AdminUser();

		adminUser.setUsername("admin");
		adminUser.setPassword(passwordEncoder.encode("123456"));
		adminUser.setJwtSecret(passwordEncoder.encode(adminUser.getUsername()));

		adminUserRepo.save(adminUser);
	}

}
