package me.dodowhat.example.seeder;

import me.dodowhat.example.repository.AdministratorRepository;
import me.dodowhat.example.model.Administrator;
import org.casbin.jcasbin.main.Enforcer;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import static me.dodowhat.example.config.security.RbacConstants.SUPER_ROLE;

@Component
public class AdministratorSeeder {

    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final Enforcer enforcer;

    public AdministratorSeeder(
            AdministratorRepository administratorRepository,
            PasswordEncoder passwordEncoder,
            Enforcer enforcer
    ) {
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.enforcer = enforcer;
    }

    @EventListener
    public void run(ContextRefreshedEvent event) {
        String username = "admin";

        if (!administratorRepository.existsByUsername(username)) {
            createAdministrator(username);
            enforcer.addRoleForUser(username, SUPER_ROLE);
        }

        username = "demo";
        if (!administratorRepository.existsByUsername(username)) {
            createAdministrator(username);
        }
    }

    private void createAdministrator(String username) {
        Administrator administrator = new Administrator();
        administrator.setUsername(username);
        administrator.setPassword(passwordEncoder.encode(username));
        administratorRepository.save(administrator);
    }
}
