package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.dto.CarDto;

import static java.util.Objects.isNull;

@Component("carConverter")
public class CarConverter implements CustomConverter<Car, CarDto> {

    @Override
    public void convert(Car source, CarDto target) {
        target.setCarType(source.getCarType());
        target.setManufacturer(source.getManufacturer());
        target.setModel(source.getModel());
        target.setPlateNumber(source.getPlateNumber());
        target.setSeatsNumber(source.getSeatNumber());
    }

    @Override
    public void reverseConvert(CarDto source, Car target) {
        if(isNull(source)){
            return;
        }
        target.setCarType(source.getCarType());
        target.setManufacturer(source.getManufacturer());
        target.setModel(source.getModel());
        target.setPlateNumber(source.getPlateNumber());
        target.setSeatNumber(source.getSeatsNumber());
    }
}
