package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.exception.SystemException;
import ua.kpi.mobiledev.repository.UserRepository;
import ua.kpi.mobiledev.util.LazyInitializationUtil;
import ua.kpi.mobiledev.web.security.model.Role;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;
import ua.kpi.mobiledev.web.security.service.CustomUserDetailsService;

import static java.util.Arrays.asList;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static ua.kpi.mobiledev.exception.ErrorCode.*;

@Service
@Transactional(readOnly = true)
public class TransactionalUserService implements UserService {

    private UserRepository userRepository;
    private CustomUserDetailsService securityDetailsRepository;
    private LazyInitializationUtil lazyInitializationUtil;

    @Autowired
    public TransactionalUserService(UserRepository userRepository, CustomUserDetailsService securityDetailsRepository,
                                    LazyInitializationUtil lazyInitializationUtil) {
        this.userRepository = userRepository;
        this.securityDetailsRepository = securityDetailsRepository;
        this.lazyInitializationUtil = lazyInitializationUtil;
    }

    @Override
    public User getById(Integer userId) {
        User user = userRepository.findOne(userId);
        if (isNull(user)) {
            throw new ResourceNotFoundException(USER_NOT_FOUND_WITH_ID, userId);
        }
        lazyInitializationUtil.initMobileNumbers(user);
        if (user instanceof TaxiDriver) {
            lazyInitializationUtil.initCar((TaxiDriver) user);
        }
        return user;
    }

    @Override
    public User getByUsername(String username) {
        return userRepository.findByEmail(username);
    }

    @Override
    @Transactional
    public User register(User user, String password) {
        checkIfUserExists(user);
        User resultUser = userRepository.save(user);
        checkIfUserSaved(resultUser);
        securityDetailsRepository.registerNewUser(prepareSecurityDetails(resultUser, password));
        return resultUser;
    }

    private void checkIfUserExists(User user) {
        if (nonNull(securityDetailsRepository.fullLoadWithAuthorities(user.getEmail()))) {
            throw new RequestException(USER_ALREADY_EXISTS, user.getEmail());
        }
    }

    private void checkIfUserSaved(User resultUser) {
        if (isNull(resultUser)) {
            throw new SystemException(REGISTRATION_GENERAL_SYSTEM_EXCEPTION);
        }
    }

    private SecurityDetails prepareSecurityDetails(User user, String password) {
        return new SecurityDetails(user.getEmail(), password, "", true,
                asList(new Role(new SimpleGrantedAuthority(user.getUserType().name()))));
    }

    @Override
    @Transactional
    public User update(User user) {
        return userRepository.save(user);
    }
}
