package com.example.scaffold.controllers.app;

import com.example.scaffold.models.AppUser;
import com.example.scaffold.repos.AppUserRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AppUsersController extends AppBaseController {

    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;

    public AppUsersController(AppUserRepo appUserRepo, PasswordEncoder passwordEncoder) {
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    public void create(@RequestBody AppUser appUser) {
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        appUser.setJwtSecret(passwordEncoder.encode(appUser.getUsername()));

        appUserRepo.save(appUser);
    }
}
