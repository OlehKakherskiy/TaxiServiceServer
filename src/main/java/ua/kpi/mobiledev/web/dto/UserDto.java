package ua.kpi.mobiledev.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.group.GroupSequenceProvider;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.web.validation.groupprovider.UserDtoGroupProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.groups.Default;
import java.util.List;

@Data
@NoArgsConstructor
@GroupSequenceProvider(UserDtoGroupProvider.class)
@JsonInclude(JsonInclude.Include.NON_NULL)
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

    public interface AddUserCheck extends Default {
    }

    public interface DriverAddMandatoryParams {
    }

    @NotNull(message = "user.name.required", groups = {AddUserCheck.class, NameCheck.class})
    @Size(min = 1, max = 45, message = "user.name.invalidSize", groups = {AddUserCheck.class, NameCheck.class})
    private String name;

    @NotNull(message = "user.email.required", groups = {AddUserCheck.class})
    @Size(min = 1, max = 100, message = "user.email.invalidSize", groups = {AddUserCheck.class})
    @Email(message = "user.email.invalidFormat", groups = {AddUserCheck.class}, regexp = MAIL_REGEX)
    private String email;

    @NotNull(message = "user.password.required", groups = {AddUserCheck.class})
    @Size(min = 1, max = 100, message = "user.password.invalidSize", groups = {AddUserCheck.class})
    private String password;

    @NotNull(message = "user.mobileNumbers.required", groups = {AddUserCheck.class})
    @Size(min = 1, message = "user.mobileNumbers.atLeastOne", groups = {AddUserCheck.class})
    @Valid
    private List<MobileNumberDto> mobileNumbers;

    @NotNull(message = "user.userType.required")
    private User.UserType userType;

    @Valid
    @NotNull(message = "user.car.required", groups = DriverAddMandatoryParams.class)
    private CarDto car;

    @Valid
    @NotNull(message = "user.driverLicense.required", groups = DriverAddMandatoryParams.class)
    private DriverLicenseDto driverLicense;
}
