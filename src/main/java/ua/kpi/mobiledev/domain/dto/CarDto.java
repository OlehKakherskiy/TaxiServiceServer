package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.Car;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDto {

    private Integer carId;

    @NotNull(message = "model.required")
    @Size(min = 1, max = 20, message = "model.invalidNameSize")
    private String model;

    @NotNull(message = "manufacturer.required")
    @Size(min = 1, max = 20, message = "manufacturer.invalidNameSize")
    private String manufacturer;

    @NotNull(message = "plateNumber.required")
    @Size(min = 1, max = 20, message = "plateNumber.invalidNameSize")
    private String plateNumber;

    @NotNull(message = "seatsNumber.required")
    @Min(value = 1, message = "seatsNumber.negativeValue")
    private Integer seatsNumber;

    @NotNull(message = "carType.required")
    private Car.CarType carType;
}
