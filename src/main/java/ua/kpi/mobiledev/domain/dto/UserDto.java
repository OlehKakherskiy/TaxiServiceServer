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

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    public static User toUser(UserDto userDto) {
        Set<MobileNumber> mobileNumbers = toMobileNumbers(userDto.getMobileNumbers());
        return isTaxiDriver(userDto.getUserType()) ?
                new TaxiDriver(null, userDto.name, userDto.username, mobileNumbers, CarDto.toCar(userDto.getCar())) :
                new User(null, userDto.name, userDto.username, userDto.userType, mobileNumbers);
    }

    private static boolean isTaxiDriver(User.UserType userType) {
        return userType == User.UserType.TAXI_DRIVER;
    }

    private static Set<MobileNumber> toMobileNumbers(List<String> mobileNumbers) {
        return mobileNumbers.stream()
                .map(s -> new MobileNumber(null, s))
                .collect(Collectors.toSet());
    }

    @NotNull(message = "name.required")
    @Size(min = 1, max = 45, message = "name.invalidSize")
    private String name;

    @NotNull(message = "username.required")
    @Size(min = 1, max = 45, message = "username.invalidSize")
    private String username;

    @NotNull(message = "password.required")
    @Size(min = 1)
    private String password;

    @NotNull(message = "mobileNumbers.required")
    private List<String> mobileNumbers;

    @NotNull(message = "userType.required")
    private User.UserType userType;

    @Valid
    private CarDto car;
}
