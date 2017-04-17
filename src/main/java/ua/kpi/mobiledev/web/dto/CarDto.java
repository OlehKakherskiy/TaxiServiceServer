package ua.kpi.mobiledev.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.group.GroupSequenceProvider;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.web.validation.groupprovider.CarDtoGroupProvider;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
@GroupSequenceProvider(CarDtoGroupProvider.class)
public class CarDto {

    public interface CarUpdateCheck extends CarModelCheck, CarManufacturerCheck, PlateNumberCheck, SeatsNumberCheck, CarTypeCheck {
    }

    public interface CarModelCheck {
    }

    public interface CarManufacturerCheck {
    }

    public interface PlateNumberCheck {
    }

    public interface SeatsNumberCheck {
    }

    public interface CarTypeCheck {
    }

    @NotNull(message = "car.model.required", groups = UserDto.AddUserCheck.class)
    @Size(min = 1, max = 20, message = "car.model.invalidSize", groups = {UserDto.AddUserCheck.class, CarModelCheck.class})
    private String model;

    @NotNull(message = "car.manufacturer.required", groups = UserDto.AddUserCheck.class)
    @Size(min = 1, max = 20, message = "car.manufacturer.invalidSize", groups = {UserDto.AddUserCheck.class, CarManufacturerCheck.class})
    private String manufacturer;

    @Size(min = 1, max = 20, message = "car.plateNumber.invalidNameSize", groups = {UserDto.AddUserCheck.class, PlateNumberCheck.class})
    @Pattern(regexp = "^([\u0410-\u042f]{2} \\d{4} [\u0410-\u042f]{2})|(\\d{3}-\\d{2} [\u0410-\u042f]{2})$"
            , message = "car.plateNumber.invalidFormat", groups = {UserDto.AddUserCheck.class, PlateNumberCheck.class})
    private String plateNumber;

    @NotNull(message = "car.seatsNumber.required", groups = UserDto.AddUserCheck.class)
    @Min(value = 1, message = "car.seatsNumber.wrongValue", groups = {UserDto.AddUserCheck.class, SeatsNumberCheck.class})
    private Integer seatsNumber;

    @NotNull(message = "car.carType.required", groups = {UserDto.AddUserCheck.class, CarTypeCheck.class})
    private Car.CarType carType;
}
