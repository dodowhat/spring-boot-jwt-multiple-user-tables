package com.example.scaffold.controllers.admin;

import com.example.scaffold.exceptions.ActionNotAllowedException;
import com.example.scaffold.models.AdminUser;
import com.example.scaffold.repos.AdminUserRepo;
import com.example.scaffold.request.AdminAuthUpdatePasswordRequestBody;
import com.example.scaffold.security.AdminUserDetails;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

import static com.example.scaffold.security.Constants.*;

@RestController
public class AdminAuthController extends AdminBaseController {

    private final AuthenticationManager authenticationManager;
    private final AdminUserRepo adminUserRepo;
    private final PasswordEncoder passwordEncoder;

    public AdminAuthController(
            AuthenticationManager authenticationManager,
            AdminUserRepo adminUserRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.adminUserRepo = adminUserRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/auth")
    public ResponseEntity<AdminUser> login(@RequestBody AdminUser request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            AdminUserDetails adminUserDetails = (AdminUserDetails) auth.getPrincipal();

            AdminUser adminUser = adminUserRepo.findByUsername(adminUserDetails.getUsername());

            byte[] secret = adminUser.getJwtSecretBytes();

            JWSSigner signer = new MACSigner(secret);
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(Long.toString(adminUser.getId()))
                    .audience(ADMIN_AUDIENCE)
                    .expirationTime(new Date(new Date().getTime() + EXPIRATION_TIME))
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            signedJWT.sign(signer);

            String token = TOKEN_PREFIX + signedJWT.serialize();

            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .body(adminUser);

        } catch (BadCredentialsException e) {
            BadCredentialsException exception = new BadCredentialsException("Invalid username or password");
            exception.initCause(e);
            throw exception;
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/auth")
    public ResponseEntity<AdminUser> profile(Authentication authentication) {
        AdminUser adminUser = adminUserRepo.findByUsername(authentication.getName());
        return ResponseEntity.ok()
                .body(adminUser);
    }

    @DeleteMapping("/auth")
    public void logout(Authentication authentication) {
        AdminUser adminUser = adminUserRepo.findByUsername(authentication.getName());
        adminUser.resetJwtSecret();
        adminUserRepo.save(adminUser);
    }

    @PatchMapping("/auth/update_password")
    public void updatePassword(
            Authentication authentication,
            @RequestBody AdminAuthUpdatePasswordRequestBody requestBody
    ) throws ActionNotAllowedException {
        AdminUser adminUser = adminUserRepo.findByUsername(authentication.getName());

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            adminUser.getUsername(),
                            requestBody.getPassword()
                    )
            );
        } catch (BadCredentialsException e) {
            ActionNotAllowedException exception = new ActionNotAllowedException("Authentication failed");
            exception.initCause(e);
            throw exception;
        }

        adminUser.setPassword(passwordEncoder.encode(requestBody.getNewPassword()));
        adminUserRepo.save(adminUser);
    }

}
