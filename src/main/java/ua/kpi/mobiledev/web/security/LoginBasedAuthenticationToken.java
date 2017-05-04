package ua.kpi.mobiledev.web.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import ua.kpi.mobiledev.web.security.model.LoginRequest;

import java.util.Collections;

public class LoginBasedAuthenticationToken extends AbstractAuthenticationToken {

    private String email;
    private String password;
    private String notificationToken;

    public LoginBasedAuthenticationToken(LoginRequest loginRequest) {
        super(Collections.emptyList());
        setAuthenticated(false);
        email = loginRequest.getUsername();
        password = loginRequest.getPassword();
        notificationToken = loginRequest.getNotificationToken();
    }

    @Override
    public String getCredentials() {
        return password;
    }

    @Override
    public String getPrincipal() {
        return email;
    }

    @Override
    public String getDetails() {
        return notificationToken;
    }
}
