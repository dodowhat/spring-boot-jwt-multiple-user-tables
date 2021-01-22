package com.example.scaffold.controllers.admin;

import com.example.scaffold.models.AdminUser;
import com.example.scaffold.repos.AdminUserRepo;
import com.example.scaffold.security.AdminUserDetails;
import com.fasterxml.jackson.annotation.JsonView;
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
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import static com.example.scaffold.security.Constants.*;

@RestController
public class AdminAuthenticationController extends AdminBaseController {

    private final AuthenticationManager authenticationManager;
    private final AdminUserRepo adminUserRepo;

    public AdminAuthenticationController(AuthenticationManager authenticationManager,
                                         AdminUserRepo adminUserRepo) {
        this.authenticationManager = authenticationManager;
        this.adminUserRepo = adminUserRepo;
    }

    @PostMapping("/authentication")
    @JsonView(AdminUser.BriefView.class)
    public ResponseEntity<AdminUser> create(@RequestBody AdminUser request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            AdminUserDetails adminUserDetails = (AdminUserDetails) auth.getPrincipal();

            AdminUser adminUser = adminUserRepo.findByUsername(adminUserDetails.getUsername());

            byte[] secret = adminUser.getJwtSecret().getBytes(StandardCharsets.UTF_8);

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
            var exception = new BadCredentialsException("用户名或密码错误");
            exception.initCause(e);
            throw exception;
        } catch (KeyLengthException e) {
            throw new RuntimeException(e);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

    }

    @GetMapping("/authentication")
    @JsonView(AdminUser.BriefView.class)
    public ResponseEntity<AdminUser> read(UsernamePasswordAuthenticationToken auth) {
        AdminUser adminUser = adminUserRepo.findByUsername((String) auth.getPrincipal());
        return ResponseEntity.ok()
                .body(adminUser);
    }

    @DeleteMapping("/authentication")
    public void delete(UsernamePasswordAuthenticationToken auth) {
        AdminUser adminUser = adminUserRepo.findByUsername((String) auth.getPrincipal());

        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        adminUser.setJwtSecret(Base64.getUrlEncoder().encodeToString(bytes));

        adminUserRepo.save(adminUser);
    }

}
