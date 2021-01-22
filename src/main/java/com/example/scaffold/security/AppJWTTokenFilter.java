package com.example.scaffold.security;

import com.example.scaffold.models.AppUser;
import com.example.scaffold.repos.AppUserRepo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import static com.example.scaffold.security.Constants.*;

@Component
public class AppJWTTokenFilter extends OncePerRequestFilter {

    private final AppUserRepo appUserRepo;

    public AppJWTTokenFilter(AppUserRepo appUserRepo) {
        this.appUserRepo = appUserRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        final String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }
        final String token = header.replace(TOKEN_PREFIX, "");

        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            final List<String> audiences = signedJWT.getJWTClaimsSet().getAudience();
            if (!audiences.contains(APP_AUDIENCE)) {
                chain.doFilter(request, response);
                return;
            }

            final String subject = signedJWT.getJWTClaimsSet().getSubject();
            AppUser appUser = appUserRepo.findById(Long.parseLong(subject));
            byte[] secret = appUser.getJwtSecret().getBytes(StandardCharsets.UTF_8);
            JWSVerifier verifier = new MACVerifier(secret);

            if (!signedJWT.verify(verifier)) {
                chain.doFilter(request, response);
                return;
            }

//            if (new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime())) {
//                chain.doFilter(request, response);
//                return;
//            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    appUser.getUsername(), null, new ArrayList<>()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);

        } catch(ParseException e) {
            throw new RuntimeException(e);
        } catch(JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
