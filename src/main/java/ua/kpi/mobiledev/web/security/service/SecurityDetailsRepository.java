package ua.kpi.mobiledev.web.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
public class SecurityDetailsRepository implements CustomUserDetailsService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetails fullLoadWithAuthorities(String username) {
        return entityManager.find(SecurityDetails.class, username);
    }

    @Override
    @Transactional
    public void registerNewUser(UserDetails newUser) {
        entityManager.persist(newUser);
    }

}
