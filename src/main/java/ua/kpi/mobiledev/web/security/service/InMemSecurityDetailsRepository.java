package ua.kpi.mobiledev.web.security.service;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.repository.inmem.DBMock;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;

import javax.annotation.Resource;

@Component("securityDetailsRepository")
public class InMemSecurityDetailsRepository implements CustomUserDetailsService{

    @Resource
    private DBMock dbMock;

    @Override
    public UserDetails fullLoadWithAuthorities(String username) {
        return dbMock.getUserDetails(username);
    }

    @Override
    public void registerNewUser(UserDetails newUser) {
        SecurityDetails securityDetails = (SecurityDetails) newUser;
        Argon2 argon2 = Argon2Factory.create();

        String plainPassword = newUser.getPassword();
        String encodedPassword = argon2.hash(2, 65536, 1, plainPassword);
        securityDetails.setPassword(encodedPassword);

        dbMock.addUserDetails(securityDetails);
    }
}
