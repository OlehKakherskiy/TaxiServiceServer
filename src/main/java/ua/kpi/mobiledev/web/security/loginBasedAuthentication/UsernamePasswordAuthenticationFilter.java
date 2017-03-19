package ua.kpi.mobiledev.web.security.loginBasedAuthentication;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.client.HttpClientErrorException;
import ua.kpi.mobiledev.web.security.model.LoginRequest;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private ObjectMapper objectMapper;

    private AuthenticationSuccessHandler successHandler;

    protected UsernamePasswordAuthenticationFilter(String defaultProcessUrl, AuthenticationSuccessHandler successHandler) {
        super(defaultProcessUrl);
        this.objectMapper = new ObjectMapper();
        this.successHandler = successHandler;
    }

    @Override
    protected boolean requiresAuthentication(HttpServletRequest request, HttpServletResponse response) {
        return true;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        if (notPostMethod(request)) {
            throw new HttpClientErrorException(BAD_REQUEST, "Authentication method not supported for " + request.getMethod());
        }
        LoginRequest loginRequest = mapFromJson(request);
        if (Objects.isNull(loginRequest)) {
            throw new HttpClientErrorException(BAD_REQUEST, "Bad request. Invalid request body");
        }
        return this.getAuthenticationManager().authenticate(mapToToken(ifValidLoginRequest(loginRequest)));
    }

    private UsernamePasswordAuthenticationToken mapToToken(LoginRequest loginRequest) {
        return new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
    }

    private LoginRequest mapFromJson(HttpServletRequest request) throws IOException {
        try {
            return objectMapper.readValue(request.getReader(), LoginRequest.class);
        } catch (JsonMappingException e) {
            return null;
        }
    }

    private LoginRequest ifValidLoginRequest(LoginRequest loginRequest) {
        if (isEmptyOrBlank(loginRequest.getUsername()) || isEmptyOrBlank(loginRequest.getPassword())) {
            throw new HttpClientErrorException(BAD_REQUEST, "Username or Password not provided");
        }
        return loginRequest;
    }

    private boolean isEmptyOrBlank(String param) {
        return Objects.isNull(param) || param.isEmpty();
    }

    private boolean notPostMethod(HttpServletRequest request) {
        return !HttpMethod.POST.name().equals(request.getMethod());
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        successHandler.onAuthenticationSuccess(request, response, authResult);
    }
}
