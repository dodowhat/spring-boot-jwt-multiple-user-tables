package me.dodowhat.example.config.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static me.dodowhat.example.config.security.JwtConstants.*;

public class JwtValidator {

    public static JWTClaimsSet getPayload(String token) {
        if (token == null) return null;

        try {
            token = token.replace(TOKEN_PREFIX, "");

            SignedJWT signedJWT = SignedJWT.parse(token);

            String secretString = GetJWTSecret();
            byte[] secret = secretString.getBytes(StandardCharsets.UTF_8);
            JWSVerifier verifier = new MACVerifier(secret);

            if (!signedJWT.verify(verifier)) {
                return null;
            }

            return signedJWT.getJWTClaimsSet();
        } catch(ParseException | JOSEException e) {
            return null;
        }
    }

    public static boolean isExpired(JWTClaimsSet jwtClaimsSet) {
        return new Date().after(jwtClaimsSet.getExpirationTime());
    }

    public static boolean validatePayloads(JWTClaimsSet jwtClaimsSet, String aud, String typ) {
        if (jwtClaimsSet == null) return false;

        try {
            if (!jwtClaimsSet.getStringClaim("typ").equals(typ)) {
                return false;
            }
            final List<String> audiences = jwtClaimsSet.getAudience();
            return audiences.contains(aud);
        } catch(ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
