package ua.kpi.mobiledev.web.security.token.extractor;

import org.springframework.security.authentication.AuthenticationServiceException;

import java.util.Objects;

public class JwtHeaderTokenExtractor implements TokenExtractor {

    private static String HEADER_PREFIX = "Bearer ";

    @Override
    public String extract(String header) {
        if (invalid(header)) {
            throw new AuthenticationServiceException("Authorization header cannot be blank!");
        }

        if (header.length() < HEADER_PREFIX.length()) {
            throw new AuthenticationServiceException("Invalid authorization header size.");
        }

        return header.substring(HEADER_PREFIX.length(), header.length());
    }

    private boolean invalid(String header) {
        return Objects.isNull(header) || header.isEmpty();
    }
}
