package ua.kpi.mobiledev.web.dto;

import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResetPasswordDto {

    @NotNull(message = "user.password.required")
    @Size(min = 1, max = 100, message = "user.password.invalidSize")
    private String password;

    @NotEmpty(message = "user.password.resetCode.required")
    private String code;
}
