package me.dodowhat.example.controller.app;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.dodowhat.example.config.exception.NotFoundException;
import me.dodowhat.example.config.security.JwtGenerator;
import me.dodowhat.example.model.User;
import me.dodowhat.example.repository.UserRepository;
import me.dodowhat.example.config.security.AppUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import static me.dodowhat.example.config.security.JwtConstants.*;
import static me.dodowhat.example.config.security.JwtConstants.REFRESH_TOKEN_HEADER;

@Api(tags = "App Authentication")
@RestController("AppAuthController")
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtGenerator jwtGenerator;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            JwtGenerator jwtGenerator
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.jwtGenerator = jwtGenerator;
    }

    @ApiOperation(value = "login", notes = "return tokens and user info")
    @PostMapping("")
    public ResponseEntity<User> login(@RequestBody User request) throws NotFoundException {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        AppUserDetails appUserDetails = (AppUserDetails) auth.getPrincipal();

        User user = userRepository.findByUsername(appUserDetails.getUsername())
                .orElseThrow(NotFoundException::new);

        String sub = Long.toString(user.getId());

        String accessToken = jwtGenerator.generate(sub, APP_AUDIENCE, ACCESS_TOKEN_TYPE);
        String refreshToken = jwtGenerator.generate(sub, APP_AUDIENCE, REFRESH_TOKEN_TYPE);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(REFRESH_TOKEN_HEADER, refreshToken)
                .body(user);

    }

    @ApiOperation("fetch authenticated user info")
    @GetMapping("")
    public ResponseEntity<User> show(UsernamePasswordAuthenticationToken auth) throws NotFoundException {
        User user = userRepository.findByUsername((String) auth.getPrincipal())
                .orElseThrow(NotFoundException::new);
        return ResponseEntity.ok()
                .body(user);
    }

}
