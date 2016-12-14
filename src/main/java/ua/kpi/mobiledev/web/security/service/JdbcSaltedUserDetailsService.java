package ua.kpi.mobiledev.web.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.web.security.model.SaltedUser;

@Component
public class JdbcSaltedUserDetailsService implements CustomUserDetailsService {

    private JdbcDaoImpl jdbcDao;

    @Autowired
    public JdbcSaltedUserDetailsService(JdbcDaoImpl jdbcDao) {
        this.jdbcDao = jdbcDao;
    }

    @Override
    public UserDetails fullLoadWithAuthorities(String username) {
        return new SaltedUser(jdbcDao.loadUserByUsername(username), getSalt(username));
    }

    private String getSalt(String username) {
        return jdbcDao.getJdbcTemplate()
                .queryForObject("select salt from users where username = ?", String.class, username);
    }

    @Override
    public void registerNewUser(UserDetails userDetailsService) {
        //TODO: implement
    }
}
