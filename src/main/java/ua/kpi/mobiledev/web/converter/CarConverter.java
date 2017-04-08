package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.dto.CarDto;

@Component("carConverter")
public class CarConverter implements CustomConverter<Car, CarDto> {

    @Override
    public void convert(Car source, CarDto target) {
        target.setCarId(source.getCarId());
        target.setCarType(source.getCarType());
        target.setManufacturer(source.getManufacturer());
        target.setModel(source.getModel());
        target.setPlateNumber(source.getPlateNumber());
        target.setSeatsNumber(source.getSeatNumber());
    }

    @Override
    public void reverseConvert(CarDto source, Car target) {
        target.setCarType(source.getCarType());
        target.setManufacturer(source.getManufacturer());
        target.setModel(source.getModel());
        target.setPlateNumber(source.getPlateNumber());
        target.setSeatNumber(source.getSeatsNumber());
    }
}
