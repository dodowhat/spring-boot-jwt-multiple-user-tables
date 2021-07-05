package me.dodowhat.example.controller.admin;

import com.nimbusds.jwt.JWTClaimsSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.dodowhat.example.config.exception.ActionNotAllowedException;
import me.dodowhat.example.config.exception.UnprocessableEntityException;
import me.dodowhat.example.config.security.JwtGenerator;
import me.dodowhat.example.config.security.JwtValidator;
import me.dodowhat.example.dto.admin.auth.LoginRequestDTO;
import me.dodowhat.example.dto.admin.auth.RefreshRequestDTO;
import me.dodowhat.example.model.Administrator;
import me.dodowhat.example.repository.AdministratorRepository;
import me.dodowhat.example.dto.admin.auth.UpdatePasswordRequestDTO;
import me.dodowhat.example.config.security.AdministratorUserDetails;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.persistence.EntityNotFoundException;

import static me.dodowhat.example.config.security.JwtConstants.*;

@Api(tags = "Admin Authentication")
@RestController("AdminAuthController")
@RequestMapping(value = "/admin/auth", produces = "application/json")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final AdministratorRepository administratorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;

    public AuthController(
            AuthenticationManager authenticationManager,
            AdministratorRepository administratorRepository,
            PasswordEncoder passwordEncoder,
            JwtGenerator jwtGenerator
    ) {
        this.authenticationManager = authenticationManager;
        this.administratorRepository = administratorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtGenerator = jwtGenerator;
    }

    @ApiOperation(value="login", notes = "Return tokens and user info")
    @PostMapping("")
    public ResponseEntity<Administrator> login(@RequestBody LoginRequestDTO requestDTO) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDTO.getUsername(),
                        requestDTO.getPassword()
                )
        );

        AdministratorUserDetails administratorUserDetails = (AdministratorUserDetails) auth.getPrincipal();

        Administrator administrator = administratorRepository.findByUsername(administratorUserDetails.getUsername())
                .orElseThrow(EntityNotFoundException::new);

        String sub = Long.toString(administrator.getId());
        String accessToken = jwtGenerator.generate(sub, ADMIN_AUDIENCE, ACCESS_TOKEN_TYPE);
        String refreshToken = jwtGenerator.generate(sub, ADMIN_AUDIENCE, REFRESH_TOKEN_TYPE);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(REFRESH_TOKEN_HEADER, refreshToken)
                .body(administrator);
    }

    @ApiOperation(value = "refresh tokens", notes = "return in http header")
    @PutMapping("")
    public ResponseEntity<Integer> refresh(@RequestBody RefreshRequestDTO requestDTO) throws UnprocessableEntityException {
        String refreshToken = requestDTO.getRefreshToken();
        String accessToken = requestDTO.getAccessToken();

        JWTClaimsSet refreshTokenJWTClaimsSet = JwtValidator.getPayload(refreshToken);

        if (!JwtValidator.validatePayloads(refreshTokenJWTClaimsSet, ADMIN_AUDIENCE, REFRESH_TOKEN_TYPE) || JwtValidator.isExpired(refreshTokenJWTClaimsSet)) {
            throw new UnprocessableEntityException("Invalid Refresh Token");
        }

        JWTClaimsSet accessTokenJWTClaimsSet = JwtValidator.getPayload(accessToken);

        if (!JwtValidator.validatePayloads(accessTokenJWTClaimsSet, ADMIN_AUDIENCE, ACCESS_TOKEN_TYPE)) {
            throw new UnprocessableEntityException("Invalid Access Token");
        }

        String refreshTokenSub = refreshTokenJWTClaimsSet.getSubject();
        String accessTokenSub = accessTokenJWTClaimsSet.getSubject();

        if (!refreshTokenSub.equals(accessTokenSub)) {
            throw new UnprocessableEntityException("Invalid Access Token");
        }

        if (!JwtValidator.isExpired(accessTokenJWTClaimsSet)) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(REFRESH_TOKEN_HEADER, refreshToken)
                    .body(null);
        }

        accessToken = jwtGenerator.generate(refreshTokenSub, ADMIN_AUDIENCE, ACCESS_TOKEN_TYPE);
        refreshToken = jwtGenerator.generate(refreshTokenSub, ADMIN_AUDIENCE, REFRESH_TOKEN_TYPE);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, accessToken)
                .header(REFRESH_TOKEN_HEADER, refreshToken)
                .body(null);
    }

    @ApiOperation("fetch authenticated user info")
    @GetMapping("")
    public ResponseEntity<Administrator> show(@ApiIgnore Authentication authentication) {
        Administrator administrator = administratorRepository.findByUsername(authentication.getName())
                .orElseThrow(EntityNotFoundException::new);
        return ResponseEntity.ok()
                .body(administrator);
    }

    @ApiOperation("update authenticated user's password")
    @PatchMapping("/password")
    public void updatePassword(
            @ApiIgnore Authentication authentication,
            @RequestBody UpdatePasswordRequestDTO requestDTO
    ) throws ActionNotAllowedException, UnprocessableEntityException {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            authentication.getName(),
                            requestDTO.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new UnprocessableEntityException("Authentication failed");
        }

        Administrator administrator = administratorRepository.findByUsername(authentication.getName())
                .orElseThrow(EntityNotFoundException::new);

        administrator.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        administratorRepository.save(administrator);
    }

}
