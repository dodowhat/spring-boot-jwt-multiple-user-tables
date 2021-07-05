package me.dodowhat.example.controller.admin;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.dodowhat.example.config.exception.ActionNotAllowedException;
import me.dodowhat.example.config.exception.NotFoundException;
import me.dodowhat.example.dto.admin.administrator.CreateRequestDTO;
import me.dodowhat.example.dto.admin.administrator.ResetPasswordResponseDTO;
import me.dodowhat.example.repository.AdministratorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import me.dodowhat.example.model.Administrator;
import springfox.documentation.annotations.ApiIgnore;

import java.security.SecureRandom;
import java.util.Base64;

@Api(tags = "Admin Administrators")
@RestController
@RequestMapping(value = "/admin/administrators", produces = "application/json")
public class AdministratorController {

	private final AdministratorRepository administratorRepository;
	private final PasswordEncoder passwordEncoder;

	public AdministratorController(
			AdministratorRepository administratorRepository,
			PasswordEncoder passwordEncoder
	) {
		this.administratorRepository = administratorRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@PostMapping("")
	public ResponseEntity<Administrator> create(@RequestBody CreateRequestDTO requestDTO) {
		Administrator administrator = new Administrator();
		administrator.setUsername(requestDTO.getUsername());
		administrator.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
		administratorRepository.save(administrator);
		return ResponseEntity.ok(administrator);
	}

	@GetMapping("")
	public ResponseEntity<Page<Administrator>> index(@RequestParam(required = false, defaultValue = "1") int page) {
		Pageable pageable = PageRequest.of(page - 1, 10);
		Page<Administrator> p = administratorRepository.findAll(pageable);
		return ResponseEntity.ok().body(p);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Administrator> show(@PathVariable long id) throws NotFoundException {
		Administrator administrator = administratorRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		return ResponseEntity.ok(administrator);
	}

	@DeleteMapping("/{id}")
	public void destroy(@ApiIgnore Authentication authentication, @PathVariable long id)
			throws NotFoundException, ActionNotAllowedException {
		Administrator authenticated = administratorRepository.findByUsername(authentication.getName())
				.orElseThrow(NotFoundException::new);
		if (authenticated.getId() == id) {
			throw new ActionNotAllowedException("Not allowed to delete yourself");
		}
		Administrator administrator = administratorRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		administratorRepository.save(administrator);
		administratorRepository.delete(administrator);
	}

	@ApiOperation("reset password")
	@PatchMapping("/{id}/password/reset")
	public ResponseEntity<ResetPasswordResponseDTO> resetPassword(
			Authentication authentication,
			@PathVariable long id
	) throws NotFoundException, ActionNotAllowedException {
		Administrator authenticated = administratorRepository.findByUsername(authentication.getName())
				.orElseThrow(NotFoundException::new);
		Administrator administrator = administratorRepository.findById(id)
				.orElseThrow(NotFoundException::new);
		if (authenticated.getId() == id) {
			throw new ActionNotAllowedException("Not allowed to reset password for yourself");
		}
		SecureRandom secureRandom = new SecureRandom();
		byte[] bytes = new byte[8];
		secureRandom.nextBytes(bytes);
		String password = Base64.getUrlEncoder().encodeToString(bytes);
		administrator.setPassword(passwordEncoder.encode(password));
		administratorRepository.save(administrator);
		return ResponseEntity.ok().body(new ResetPasswordResponseDTO(password));
	}

}
