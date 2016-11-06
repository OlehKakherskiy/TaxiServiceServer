package ua.kpi.mobiledev.service;

import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.User;

/**
 * Created by Oleg on 06.11.2016.
 */
public interface UserService {

    User getUser(Integer userId);

}
