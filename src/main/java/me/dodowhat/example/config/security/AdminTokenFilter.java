package me.dodowhat.example.config.security;

import com.nimbusds.jwt.JWTClaimsSet;
import me.dodowhat.example.repository.AdministratorRepository;
import me.dodowhat.example.model.Administrator;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.persistence.EntityNotFoundException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;

import static me.dodowhat.example.config.security.JwtConstants.*;

@Component
public class AdminTokenFilter extends OncePerRequestFilter {

    private final AdministratorRepository administratorRepository;

    public AdminTokenFilter(
            AdministratorRepository administratorRepository
    ) {
        this.administratorRepository = administratorRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        final String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token == null) {
            chain.doFilter(request, response);
            return;
        }

        JWTClaimsSet jwtClaimsSet = JwtValidator.getPayload(token);

        if (jwtClaimsSet == null) {
            chain.doFilter(request, response);
            return;
        }

        if (!JwtValidator.validatePayloads(jwtClaimsSet, ADMIN_AUDIENCE, ACCESS_TOKEN_TYPE) || JwtValidator.isExpired(jwtClaimsSet)) {
            chain.doFilter(request, response);
            return;
        }

        final String sub = jwtClaimsSet.getSubject();
        Administrator administrator = administratorRepository.findById(Long.parseLong(sub))
                .orElseThrow(EntityNotFoundException::new);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                administrator.getUsername(), null, new HashSet<>()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
