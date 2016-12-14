package ua.kpi.mobiledev.web.security.model;

import lombok.Getter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
public class SaltedUser extends User {

    private String salt;

    public SaltedUser(UserDetails user, String salt) {
        super(user.getUsername(), user.getPassword(), user.getAuthorities());
        this.salt = salt;
    }
}
