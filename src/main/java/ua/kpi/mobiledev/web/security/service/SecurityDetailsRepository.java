package ua.kpi.mobiledev.web.security.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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

    @Override
    public UserDetails fullLoadWithAuthorities(String username) {
        return entityManager.find(SecurityDetails.class, username);
    }

    @Override
    @Transactional
    public void registerNewUser(UserDetails newUser) {
        SecurityDetails securityDetails = (SecurityDetails) newUser;
        Argon2 argon2 = Argon2Factory.create();

        String plainPassword = newUser.getPassword();
        String encodedPassword = argon2.hash(2, 65536, 1, plainPassword);
        securityDetails.setPassword(encodedPassword);
        entityManager.persist(securityDetails);
    }
}
