package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.UserRepository;

import java.text.MessageFormat;
import java.util.Objects;

@Service
public class TransactionalUserService implements UserService {

    private UserRepository userRepository;

    @Autowired
    public TransactionalUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public TransactionalUserService() {
    }

    @Override
    public User getUser(Integer userId) {
        User result = userRepository.findOne(userId);
        return Objects.requireNonNull(result, MessageFormat.format("There''s no user with id = ''{0}''", userId));
    }
}
