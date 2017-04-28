package ua.kpi.mobiledev.web.security.service;

public interface CustomerUserDetailsCrudService extends CustomUserDetailsService {

    void updatePassword(String email, String password);
}
