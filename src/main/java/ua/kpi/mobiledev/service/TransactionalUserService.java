package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.UserRepository;
import ua.kpi.mobiledev.web.security.model.Role;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;
import ua.kpi.mobiledev.web.security.service.CustomUserDetailsService;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Objects;

@Service
public class TransactionalUserService implements UserService {

    private UserRepository userRepository;

    private CustomUserDetailsService securityDetailsRepository;

    @Autowired
    public TransactionalUserService(UserRepository userRepository, CustomUserDetailsService securityDetailsRepository) {
        this.userRepository = userRepository;
        this.securityDetailsRepository = securityDetailsRepository;
    }

    @Override
    public User getById(Integer userId) {
        User result = userRepository.findOne(userId);
        return Objects.requireNonNull(result, MessageFormat.format("There''s no user with id = ''{0}''", userId));
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    @Transactional
    public User register(User user, String password) {
        if (isUserExists(user)) {
            throw new IllegalArgumentException("There's user with current email: " + user.getEmail());
        }
        User resultUser = userRepository.save(user);
        if (Objects.nonNull(resultUser)) {
            securityDetailsRepository.registerNewUser(prepareSecurityDetails(user, password));
        }
        return resultUser;
    }

    private UserDetails prepareSecurityDetails(User user, String password) {
        return new SecurityDetails(user.getEmail(), password, "", true,
                Arrays.asList(new Role(new SimpleGrantedAuthority(user.getUserType().name()))));
    }

    private boolean isUserExists(User user) {
        return Objects.nonNull(securityDetailsRepository.fullLoadWithAuthorities(user.getEmail()));
    }
}
