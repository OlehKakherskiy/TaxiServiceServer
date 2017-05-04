package ua.kpi.mobiledev.web.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.web.security.model.UserContext;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenFactory {

    public AccessJwtToken createAccessJwtToken(UserContext userContext, List<GrantedAuthority> roles, Key digestKey) {
        Claims claims = prepareClaims(userContext, roles);
        Date currentTime = new Date();
        Date expirationTime = new Date(currentTime.getTime() + 24 * 3600 * 1000);
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer("/taxiService")
                .setIssuedAt(new Date())
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS512, digestKey)
                .compact();

        return new AccessJwtToken(token, claims);
    }

    private Claims prepareClaims(UserContext userContext, List<GrantedAuthority> roles) {
        Claims claims = Jwts.claims();
        claims.setSubject(userContext.getUsername());
        claims.put("userId", userContext.getUser().getId());
        claims.put("userType", userContext.getUserType());
        claims.put("scopes", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return claims;
    }
}
