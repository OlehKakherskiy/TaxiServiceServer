package ua.kpi.mobiledev.web.security.loginBasedAuthentication;

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.web.security.model.UserContext;
import ua.kpi.mobiledev.web.security.token.AccessJwtToken;
import ua.kpi.mobiledev.web.security.token.JwtTokenFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UsernamePasswordAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;

    private final JwtTokenFactory tokenFactory;

    @Autowired
    public UsernamePasswordAuthenticationSuccessHandler(JwtTokenFactory tokenFactory) {
        this.mapper = new ObjectMapper();
        this.tokenFactory = tokenFactory;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserContext userContext = (UserContext) authentication.getPrincipal();
        List<GrantedAuthority> grantedAuthorityList = (List<GrantedAuthority>) authentication.getAuthorities();

        AccessJwtToken accessToken = tokenFactory.createAccessJwtToken(userContext, grantedAuthorityList);
//        JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), getTokenMap(accessToken, userContext.getUserType()));

        clearAuthenticationAttributes(request);
    }

    private Map<String, String> getTokenMap(AccessJwtToken accessToken, User.UserType userType) {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", accessToken.getToken());
        tokenMap.put("token_type", "Bearer");
        tokenMap.put("user_type", userType.name());
//        tokenMap.put("refreshToken", refreshToken.getToken());
        return tokenMap;
    }

    /**
     * Removes temporary authentication-related data which may have been stored
     * in the session during the authentication process..
     */
    protected final void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
    }
}
