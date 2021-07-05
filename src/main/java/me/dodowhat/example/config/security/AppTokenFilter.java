package me.dodowhat.example.config.security;

import me.dodowhat.example.repository.UserRepository;
import me.dodowhat.example.model.User;
import com.nimbusds.jwt.JWTClaimsSet;
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
public class AppTokenFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    public AppTokenFilter(
            UserRepository userRepository
    ) {
        this.userRepository = userRepository;
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

        if (!JwtValidator.validatePayloads(jwtClaimsSet, APP_AUDIENCE, ACCESS_TOKEN_TYPE) || JwtValidator.isExpired(jwtClaimsSet)) {
            chain.doFilter(request, response);
            return;
        }

        final String sub = jwtClaimsSet.getSubject();
        User user = userRepository.findById(Long.parseLong(sub))
                .orElseThrow(EntityNotFoundException::new);
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user.getUsername(), null, new HashSet<>()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
