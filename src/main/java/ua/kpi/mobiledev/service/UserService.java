package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.User;

public interface UserService {

    User getById(Integer userId);

    User getByUsername(String username);

    User register(User user, String password);

    User update(User user);
}
