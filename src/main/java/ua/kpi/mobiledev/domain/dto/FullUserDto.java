package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FullUserDto {

    public static FullUserDto toUserDto(User user) {
        FullUserDto fullUserDto = new FullUserDto();
        fullUserDto.setUserId(user.getId());
        fullUserDto.setName(user.getName());
        fullUserDto.setEmail(user.getEmail());
        fullUserDto.setUserType(user.getUserType());
        fullUserDto.setMobileNumbers(user.getMobileNumbers().stream().map(MobileNumberDto::from).collect(Collectors.toList()));
        if (isTaxiDriver(user.getUserType())) {
            fullUserDto.setCar(CarDto.fromCar(((TaxiDriver) user).getCar()));
        }
        return fullUserDto;
    }

    private static boolean isTaxiDriver(User.UserType userType) {
        return userType == User.UserType.TAXI_DRIVER;
    }

    @NotNull
    @Min(1)
    private Integer userId;

    @NotNull(message = "name.required")
    @Size(min = 1, max = 45, message = "name.invalidSize")
    private String name;

    @NotNull(message = "username.required")
    @Size(min = 1, max = 45, message = "username.invalidSize")
    private String email;

    @NotNull(message = "userType.required")
    private User.UserType userType;

    @NotNull(message = "mobileNumbers.required")
    private List<MobileNumberDto> mobileNumbers;

    @Valid
    private CarDto car;
}
