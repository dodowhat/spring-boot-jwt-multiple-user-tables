package me.dodowhat.example.config.security;

import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import static me.dodowhat.example.config.security.JwtConstants.*;

@Data
@Component
public class JwtGenerator {
    private final JWSSigner jwsSigner;
    private final JWSHeader jwsHeader;

    public JwtGenerator() throws KeyLengthException {
        String secretString = GetJWTSecret();
        byte[] secret = secretString.getBytes(StandardCharsets.UTF_8);
        this.jwsSigner = new MACSigner(secret);
        this.jwsHeader = new JWSHeader(JWSAlgorithm.HS256);
    }

    public String generate(String sub, String aud, String typ) {
        try {
            long duration;
            duration = typ.equals(REFRESH_TOKEN_TYPE) ? REFRESH_TOKEN_DURATION : ACCESS_TOKEN_DURATION;

            JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                    .subject(sub)
                    .audience(aud)
                    .expirationTime(new Date(new Date().getTime() + duration))
                    .claim("typ", typ)
                    .build();
            SignedJWT signedJWT = new SignedJWT(jwsHeader, jwtClaimsSet);
            signedJWT.sign(jwsSigner);
            return TOKEN_PREFIX + signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
}
