package ua.kpi.mobiledev.web.security.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class LoginRequest {

    private String username;
    private String password;
    private String notificationToken;

    @JsonCreator
    public LoginRequest(@JsonProperty("email") String username, @JsonProperty("password") String password,
                        @JsonProperty("notificationToken") String notificationToken) {
        this.username = username;
        this.password = password;
        this.notificationToken = notificationToken;
    }
}
