package me.dodowhat.example.config.security;

import me.dodowhat.example.repository.UserRepository;
import me.dodowhat.example.model.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;

@Service
public class AppUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(EntityNotFoundException::new);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AppUserDetails(
                user.getUsername(),
                user.getPassword(),
                new HashSet<>()
        );
    }
}
