package com.example.scaffold.controllers;

import com.example.scaffold.controllers.AppBaseController;
import com.example.scaffold.models.AppUser;
import com.example.scaffold.repos.AppUserRepo;
import com.example.scaffold.security.AppUserDetails;
import com.fasterxml.jackson.annotation.JsonView;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import static com.example.scaffold.security.Constants.*;

@RestController
public class AppAuthController extends AppBaseController {

    private final AuthenticationManager authenticationManager;
    private final AppUserRepo appUserRepo;

    public AppAuthController(AuthenticationManager authenticationManager,
                             AppUserRepo appUserRepo) {
        this.authenticationManager = authenticationManager;
        this.appUserRepo = appUserRepo;
    }

    @PostMapping("/auth")
    @JsonView(AppUser.BriefView.class)
    public ResponseEntity<AppUser> login(@RequestBody AppUser request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            AppUserDetails appUserDetails = (AppUserDetails) auth.getPrincipal();

            AppUser appUser = appUserRepo.findByUsername(appUserDetails.getUsername());

            byte[] secret = appUser.getJwtSecretBytes();

            JWSSigner signer = new MACSigner(secret);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(Long.toString(appUser.getId()))
                    .audience(APP_AUDIENCE)
                    .expirationTime(new Date(new Date().getTime() + EXPIRATION_TIME))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            String token = TOKEN_PREFIX + signedJWT.serialize();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(appUser);

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("/auth")
    @JsonView(AppUser.BriefView.class)
    public ResponseEntity<AppUser> profile(UsernamePasswordAuthenticationToken auth) {
        AppUser appUser = appUserRepo.findByUsername((String) auth.getPrincipal());
        return ResponseEntity.ok()
                .body(appUser);
    }

    @DeleteMapping("/auth")
    public void logout(UsernamePasswordAuthenticationToken auth) {
        AppUser appUser = appUserRepo.findByUsername((String) auth.getPrincipal());

        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        appUser.setJwtSecret(Base64.getUrlEncoder().encodeToString(bytes));

        appUserRepo.save(appUser);
    }

}
