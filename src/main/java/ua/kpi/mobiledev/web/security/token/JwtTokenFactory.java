package ua.kpi.mobiledev.web.security.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.web.security.model.UserContext;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenFactory {

    public AccessJwtToken createAccessJwtToken(UserContext userContext, List<GrantedAuthority> roles) {
        Claims claims = prepareClaims(userContext, roles);
        LocalDateTime currentTime = LocalDateTime.now(); //TODO: add HS512 to header if needed
        String token = Jwts.builder()
                .setClaims(claims)
                .setIssuer("/taxiService")
                .setIssuedAt(new Date())
                .setExpiration(dateTimeFrom(currentTime.plusMinutes(24 * 60)))
                .signWith(SignatureAlgorithm.HS512, "stub") //TODO: change to another value
                .compact();

        return new AccessJwtToken(token, claims);
    }

    private Claims prepareClaims(UserContext userContext, List<GrantedAuthority> roles) {
        Claims claims = Jwts.claims();
        claims.setSubject(userContext.getUsername());
        claims.put("userId", userContext.getId());
        claims.put("userType", userContext.getUserType());
        claims.put("scopes", roles.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return claims;
    }

    private Date dateTimeFrom(LocalDateTime currentTime) {
        return new Date(currentTime.getYear(), currentTime.getMonthValue(), currentTime.getDayOfMonth(),
                currentTime.getHour(), currentTime.getMinute(), currentTime.getSecond());
    }
}
