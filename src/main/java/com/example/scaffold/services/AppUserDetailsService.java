package com.example.scaffold.services;

import com.example.scaffold.models.AppUser;
import com.example.scaffold.repos.AppUserRepo;
import com.example.scaffold.security.AppUserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final AppUserRepo appUserRepo;

    public AppUserDetailsService(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    public AppUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepo.findByUsername(username);
        if (appUser == null) {
            throw new UsernameNotFoundException(username);
        }
        return new AppUserDetails(appUser.getId(),
                appUser.getJwtSecret(),
                appUser.getUsername(),
                appUser.getPassword(),
                new ArrayList<>()
        );
    }
}
