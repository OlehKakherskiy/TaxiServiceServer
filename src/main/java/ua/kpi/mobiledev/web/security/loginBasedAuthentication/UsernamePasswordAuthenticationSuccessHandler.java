package ua.kpi.mobiledev.web.security.loginBasedAuthentication;

import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;
import com.auth0.jwt.internal.org.bouncycastle.crypto.prng.RandomGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.NotificationTokenRepository;
import ua.kpi.mobiledev.web.security.model.TokenStoreObject;
import ua.kpi.mobiledev.web.security.model.UserContext;
import ua.kpi.mobiledev.web.security.service.RedisStoreService;
import ua.kpi.mobiledev.web.security.token.AccessJwtToken;
import ua.kpi.mobiledev.web.security.token.JwtTokenFactory;

import javax.annotation.Resource;
import javax.crypto.KeyGenerator;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UsernamePasswordAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final ObjectMapper mapper;

    private final JwtTokenFactory tokenFactory;

    private final RedisStoreService<String, TokenStoreObject> redisStoreService;

    @Resource(name = "notificationTokenRepository")
    private NotificationTokenRepository notificationTokenRepository;

    @Autowired
    private RandomGenerator randomGenerator;

    public UsernamePasswordAuthenticationSuccessHandler(JwtTokenFactory tokenFactory,
                                                        RedisStoreService<String, TokenStoreObject> redisStoreService) {
        this.mapper = new ObjectMapper();
        this.tokenFactory = tokenFactory;
        this.redisStoreService = redisStoreService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        UserContext userContext = (UserContext) authentication.getPrincipal();
        List<GrantedAuthority> grantedAuthorityList = (List<GrantedAuthority>) authentication.getAuthorities();
        Key digestKey = null;
        try {
            digestKey = KeyGenerator.getInstance("HmacSHA512").generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        AccessJwtToken accessToken = tokenFactory.createAccessJwtToken(userContext, grantedAuthorityList, digestKey);
//        JwtToken refreshToken = tokenFactory.createRefreshToken(userContext);

        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        mapper.writeValue(response.getWriter(), getTokenMap(accessToken, userContext.getUserType(), userContext.getUser().getId()));

        saveToTokenStore(accessToken.getToken(), createTokenStoreObject(accessToken, digestKey));
        saveNotificationToken(userContext.getUser(), (String) authentication.getDetails());
        clearAuthenticationAttributes(request);
    }

    private void saveToTokenStore(String token, TokenStoreObject tokenStoreObject) {
        redisStoreService.save(token, tokenStoreObject);
    }

    private TokenStoreObject createTokenStoreObject(AccessJwtToken accessToken, Key digestKey) {
        return new TokenStoreObject(true, accessToken.getClaims().getExpiration(), digestKey);
    }

    private Map<String, String> getTokenMap(AccessJwtToken accessToken, User.UserType userType, Integer userId) {
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", accessToken.getToken());
        tokenMap.put("token_type", "Bearer");
        tokenMap.put("user_type", userType.name());
        tokenMap.put("id", userId.toString());
//        tokenMap.put("refreshToken", refreshToken.getToken());
        return tokenMap;
    }

    private void saveNotificationToken(User user, String notificationToken) {
        notificationTokenRepository.saveNotificationToken(user, notificationToken);
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
