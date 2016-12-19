package ua.kpi.mobiledev.web.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class SecurityDetailsRepository implements CustomUserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public SecurityDetailsRepository(BCryptPasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails fullLoadWithAuthorities(String username) {
        return entityManager.find(SecurityDetails.class, username);
    }

    @Override
    @Transactional
    public void registerNewUser(UserDetails newUser) {
        SecurityDetails securityDetails = (SecurityDetails) newUser;
        String plainPassword = newUser.getPassword();
        String encodedPassword = passwordEncoder.encode(plainPassword);
        securityDetails.setPassword(encodedPassword);
        entityManager.persist(securityDetails);
    }
}
