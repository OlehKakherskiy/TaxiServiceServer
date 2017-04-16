package ua.kpi.mobiledev.domain.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.group.GroupSequenceProvider;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.web.validation.groupprovider.UserDtoGroupProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@NoArgsConstructor
@GroupSequenceProvider(UserDtoGroupProvider.class)
public class UserDto {

    private static final String MAIL_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")" +
            "@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.)" +
            "{3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|" +
            "\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    public interface UpdateUserCheck extends NameCheck, CarDto.CarUpdateCheck, DriverLicenseDto.DriverLicenseCheck {
    }

    public interface NameCheck {
    }

    public interface AddUserCheck extends DriverAddMandatoryParams {
    }

    public interface DriverAddMandatoryParams {
    }

    @NotNull(message = "name.required", groups = {AddUserCheck.class, NameCheck.class})
    @Size(min = 1, max = 45, message = "name.invalidSize", groups = {AddUserCheck.class, NameCheck.class})
    private String name;

    @NotNull(message = "email.required", groups = {AddUserCheck.class})
    @Size(min = 1, max = 100, message = "email.invalidSize", groups = {AddUserCheck.class})
    @Email(message = "email.invalidFormat", groups = {AddUserCheck.class}, regexp = MAIL_REGEX)
    private String email;

    @NotNull(message = "password.required", groups = {AddUserCheck.class})
    @Size(min = 1, max = 100, groups = {AddUserCheck.class})
    private String password;

    @NotNull(message = "mobileNumbers.required", groups = {AddUserCheck.class})
    @Size(min = 1, message = "mobileNumbers.atLeastOne", groups = {AddUserCheck.class})
    @Valid
    private List<MobileNumberDto> mobileNumbers;

    @NotNull(message = "userType.required")
    private User.UserType userType;

    @Valid
    @NotNull(message = "car.required", groups = DriverAddMandatoryParams.class)
    private CarDto car;

    @Valid
    @NotNull(message = "driverLicense.required", groups = DriverAddMandatoryParams.class)
    private DriverLicenseDto driverLicense;
}
