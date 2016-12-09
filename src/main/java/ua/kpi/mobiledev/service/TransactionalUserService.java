package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.UserRepository;

import javax.annotation.Resource;

/**
 * Created by Oleg on 06.11.2016.
 */
@Service
public class TransactionalUserService implements UserService {

    @Autowired
    private UserRepository userRepository;

    public TransactionalUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TransactionalUserService() {
    }

    @Override
    public User getUser(Integer userId) {
        return null;
    }
}
