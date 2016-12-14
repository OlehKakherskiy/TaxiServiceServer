package ua.kpi.mobiledev.web.security.service;

import org.springframework.security.core.userdetails.UserDetails;

public interface CustomUserDetailsService {

    UserDetails fullLoadWithAuthorities(String username);

    void registerNewUser(UserDetails userDetailsService);
}
