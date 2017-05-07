package ua.kpi.mobiledev.web.security.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class SecurityDetailsRepositoryImpl implements CustomerUserDetailsCrudService {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public UserDetails fullLoadWithAuthorities(String username) {
        return entityManager.find(SecurityDetails.class, username);
    }

    @Override
    @Transactional
    public void updatePassword(String email, String password) {
        SecurityDetails securityDetails = (SecurityDetails) fullLoadWithAuthorities(email);
        securityDetails.setPassword(hashPassword(password));
        entityManager.merge(securityDetails);
    }

    @Override
    @Transactional
    public void registerNewUser(UserDetails newUser) {
        SecurityDetails securityDetails = (SecurityDetails) newUser;
        securityDetails.setPassword(hashPassword(newUser.getPassword()));
        entityManager.persist(securityDetails);
    }

    private String hashPassword(String password) {
        Argon2 argon2 = Argon2Factory.create();
        return argon2.hash(2, 65536, 1, password);
    }
}
