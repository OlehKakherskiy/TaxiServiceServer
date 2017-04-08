package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    @NotNull(message = "name.required")
    @Size(min = 1, max = 45, message = "name.invalidSize")
    private String name;

    @NotNull(message = "email.required")
    @Size(min = 1, max = 100, message = "email.invalidSize")
    private String email;

    @NotNull(message = "password.required")
    @Size(min = 1, max = 100)
    private String password;

//    @NotNull(message = "mobileNumbers.required")
    private List<MobileNumberDto> mobileNumbers;

    @NotNull(message = "userType.required")
    private User.UserType userType;

    @Valid
    private CarDto car;

    //    @Valid
    private DriverLicenseDto driverLicense;
}
