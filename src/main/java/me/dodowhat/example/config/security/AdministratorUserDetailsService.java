package me.dodowhat.example.config.security;

import me.dodowhat.example.repository.AdministratorRepository;
import me.dodowhat.example.model.Administrator;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import java.util.HashSet;

@Service
public class AdministratorUserDetailsService implements UserDetailsService {

    private final AdministratorRepository administratorRepository;

    public AdministratorUserDetailsService(AdministratorRepository administratorRepository) {
        this.administratorRepository = administratorRepository;
    }

    @Override
    public AdministratorUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Administrator administrator = administratorRepository.findByUsername(username)
                .orElseThrow(EntityExistsException::new);
        if (administrator == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AdministratorUserDetails(
                administrator.getUsername(),
                administrator.getPassword(),
                new HashSet<>()
        );
    }
}
