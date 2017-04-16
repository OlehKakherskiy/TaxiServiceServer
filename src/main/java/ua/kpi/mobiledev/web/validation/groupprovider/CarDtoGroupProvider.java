package ua.kpi.mobiledev.web.validation.groupprovider;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import ua.kpi.mobiledev.domain.dto.CarDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class CarDtoGroupProvider implements DefaultGroupSequenceProvider<CarDto> {

    @Override
    public List<Class<?>> getValidationGroups(CarDto carDto) {
        List<Class<?>> groups = new ArrayList<>();
//        groups.add(UserDto.class);
        groups.add(CarDto.class);

        if (isNull(carDto)) {
            return groups;
        }
        if (nonNull(carDto.getModel())) {
            groups.add(CarDto.CarModelCheck.class);
        }
        if (nonNull(carDto.getManufacturer())) {
            groups.add(CarDto.CarManufacturerCheck.class);
        }
        if (nonNull(carDto.getPlateNumber())) {
            groups.add(CarDto.PlateNumberCheck.class);
        }
        if (nonNull(carDto.getSeatsNumber())) {
            groups.add(CarDto.SeatsNumberCheck.class);
        }
        return groups;
    }
}
