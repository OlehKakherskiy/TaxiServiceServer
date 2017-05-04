package ua.kpi.mobiledev.web.security.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.kpi.mobiledev.domain.User;

@Data
@AllArgsConstructor
public class UserContext {

    private User user;

    private String username;

    private User.UserType userType;

}
