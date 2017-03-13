package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;
import static ua.kpi.mobiledev.domain.dto.CarDto.toCar;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationUserDto {

    @NotNull(message = "name.required")
    @Size(min = 1, max = 45, message = "name.invalidSize")
    private String name;

    @NotNull(message = "email.required")
    @Size(min = 1, max = 45, message = "email.invalidSize")
    private String email;

    @NotNull(message = "password.required")
    @Size(min = 1)
    private String password;

    @NotNull(message = "mobileNumbers.required")
    private List<String> mobileNumbers;

    @NotNull(message = "userType.required")
    private User.UserType userType;

    @Valid
    private CarDto car;

    //    @Valid
    private DriverLicenseDto driverLicense;

    public static User toUser(RegistrationUserDto registrationUserDto) {
        Set<MobileNumber> mobileNumbers = toMobileNumbers(registrationUserDto.getMobileNumbers());
        return registrationUserDto.getUserType() == TAXI_DRIVER ?
                new TaxiDriver(null, registrationUserDto.name, registrationUserDto.email, mobileNumbers,
                        toCar(registrationUserDto.car)/*,toDriverLicense(registrationUserDto.driverLicense)//todo:uncomment when driver license feature will be ready*/,null) :
                new User(null, registrationUserDto.name, registrationUserDto.email, registrationUserDto.userType, mobileNumbers);
    }

    private static Set<MobileNumber> toMobileNumbers(List<String> mobileNumbers) {
        return mobileNumbers.stream()
                .map(mobileNumber -> new MobileNumber(null, mobileNumber))
                .collect(Collectors.toSet());
    }
}
