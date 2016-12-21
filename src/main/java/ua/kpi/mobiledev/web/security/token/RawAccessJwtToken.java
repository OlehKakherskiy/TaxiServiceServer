package ua.kpi.mobiledev.web.security.token;

import io.jsonwebtoken.*;
import org.springframework.security.authentication.BadCredentialsException;

import java.security.Key;

public class RawAccessJwtToken implements JwtToken {

    private String token;

    public RawAccessJwtToken(String token) {
        this.token = token;
    }

    public Jws<Claims> parseClaims(Key signingKey) {
        try {
            return Jwts.parser().setSigningKey(signingKey).parseClaimsJws(this.token);
        } catch (UnsupportedJwtException | MalformedJwtException | IllegalArgumentException | SignatureException ex) {
            throw new BadCredentialsException("Invalid JWT token: ", ex);
        } catch (ExpiredJwtException expiredEx) {
            throw new SecurityException("Token expired");
        }
    }

    @Override
    public String getToken() {
        return token;
    }
}
