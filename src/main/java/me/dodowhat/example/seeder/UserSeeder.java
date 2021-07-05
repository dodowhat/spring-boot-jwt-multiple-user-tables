package me.dodowhat.example.seeder;

import me.dodowhat.example.model.User;
import me.dodowhat.example.repository.UserRepository;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class UserSeeder {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserSeeder(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @EventListener
    public void run(ContextRefreshedEvent event) {
        String username = "user";

        if (!userRepository.existsByUsername(username)) {
            createUser(username);
        }
    }

    private void createUser(String username) {
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(username));
        userRepository.save(user);
    }
}
