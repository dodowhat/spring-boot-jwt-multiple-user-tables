package com.example.scaffold.controllers.admin;

import com.example.scaffold.exceptions.ActionNotAllowedException;
import com.example.scaffold.models.AdminRole;
import com.example.scaffold.repos.AdminRoleRepo;
import com.example.scaffold.repos.AdminUserRepo;
import com.example.scaffold.request.AdminUserAssignRolesRequestBody;
import com.example.scaffold.response.AdminUserResetPasswordResponseBody;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.example.scaffold.models.AdminUser;

import javax.persistence.EntityNotFoundException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@RestController
public class AdminUserController extends AdminBaseController {

	private final AdminUserRepo adminUserRepo;
	private final PasswordEncoder passwordEncoder;
	private final AdminRoleRepo adminRoleRepo;

	public AdminUserController(
			AdminUserRepo adminUserRepo,
			PasswordEncoder passwordEncoder,
			AdminRoleRepo adminRoleRepo
	) {
		this.adminUserRepo = adminUserRepo;
		this.passwordEncoder = passwordEncoder;
		this.adminRoleRepo = adminRoleRepo;
	}
	
	@PostMapping("/admin_users")
	public void create(@RequestBody AdminUser adminUser) {
		adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
		adminUser.resetJwtSecret();
		adminUserRepo.save(adminUser);
	}

	@GetMapping("/admin_users")
	public ResponseEntity<Page<AdminUser>> index() {
		Pageable pageable = PageRequest.of(0, 10);
		Page<AdminUser> page = adminUserRepo.findAll(pageable);
		return ResponseEntity.ok().body(page);
	}

	@GetMapping("/admin_users/{id}")
	public ResponseEntity<AdminUser> show(@PathVariable long id) {
	    if (!adminUserRepo.existsById(id)) {
	    	throw new EntityNotFoundException("Entity not found");
		}
		AdminUser adminUser = adminUserRepo.findById(id);
		return ResponseEntity.ok(adminUser);
	}

	@DeleteMapping("/admin_users/{id}")
	public void destroy(Authentication authentication, @PathVariable long id) throws ActionNotAllowedException {
		if (!adminUserRepo.existsById(id)) {
			throw new EntityNotFoundException("Entity not found");
		}
		AdminUser currentUser = adminUserRepo.findByUsername(authentication.getName());
		if (currentUser.getId() == id) {
			throw new ActionNotAllowedException("Not allowed to delete yourself");
		}
		AdminUser adminUser = adminUserRepo.findById(id);
		adminUser.setRoles(new HashSet<>());
		adminUserRepo.save(adminUser);
		adminUserRepo.delete(adminUser);
	}

	@PatchMapping("/admin_users/{id}/reset_password")
	public ResponseEntity<AdminUserResetPasswordResponseBody> resetPassword(
			Authentication authentication,
			@PathVariable long id
	) throws ActionNotAllowedException {
		AdminUser currentUser = adminUserRepo.findByUsername(authentication.getName());
		if (currentUser.getId() == id) {
			throw new ActionNotAllowedException("Not allowed to reset password for yourself");
		}
		if (!adminUserRepo.existsById(id)) {
			throw new EntityNotFoundException("Entity not found");
		}
		AdminUser adminUser = adminUserRepo.findById(id);
		SecureRandom secureRandom = new SecureRandom();
		byte[] bytes = new byte[8];
		secureRandom.nextBytes(bytes);
		String password = Base64.getUrlEncoder().encodeToString(bytes);
		adminUser.setPassword(passwordEncoder.encode(password));
		adminUser.resetJwtSecret();
		adminUserRepo.save(adminUser);
		return ResponseEntity.ok().body(new AdminUserResetPasswordResponseBody(password));
	}

	@PatchMapping("/admin_users/{id}/assign_roles")
	public void assignRoles(
			@PathVariable long id,
			@RequestBody AdminUserAssignRolesRequestBody requestBody
	) throws ActionNotAllowedException {
		AdminUser adminUser = adminUserRepo.findById(id);
		if (adminUser.isAdmin()) {
			AdminRole adminRole = adminRoleRepo.findByName("admin");
			if (!requestBody.getRoleIds().contains(adminRole.getId())) {
			    throw new ActionNotAllowedException("Not allowed to detach the last admin role user");
			}
		}
		Set<AdminRole> roles = adminRoleRepo.findByIdIn(requestBody.getRoleIds());
		adminUser.setRoles(roles);
		adminUserRepo.save(adminUser);
	}

}
