package ua.kpi.mobiledev.web.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import ua.kpi.mobiledev.service.UserService;
import ua.kpi.mobiledev.web.security.model.TokenStoreObject;
import ua.kpi.mobiledev.web.security.service.RedisStoreService;
import ua.kpi.mobiledev.web.security.token.RawAccessJwtToken;
import ua.kpi.mobiledev.web.security.token.extractor.TokenExtractor;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LogoutFilter extends AbstractAuthenticationProcessingFilter {

    private RedisStoreService<String, TokenStoreObject> redisStoreService;

    @Resource(name = "userService")
    private UserService userService;

    private final TokenExtractor tokenExtractor;

    @Autowired
    protected LogoutFilter(RedisStoreService<String, TokenStoreObject> redisStoreService, TokenExtractor tokenExtractor) {
        super("/logout");
        this.redisStoreService = redisStoreService;
        this.tokenExtractor = tokenExtractor;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        String tokenPayload = request.getHeader("Authorization");
        RawAccessJwtToken token = new RawAccessJwtToken(tokenExtractor.extract(tokenPayload));

        TokenStoreObject tokenStoreObject = redisStoreService.get(token.getToken());
        tokenStoreObject.setValid(false);
        redisStoreService.save(token.getToken(), tokenStoreObject);
        return null;
    }
}
