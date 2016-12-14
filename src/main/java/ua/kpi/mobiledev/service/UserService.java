package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.User;

public interface UserService {

    User getById(Integer userId);

    User getByUsername(String username);

}
