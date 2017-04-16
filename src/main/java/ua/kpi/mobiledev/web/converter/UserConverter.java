package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.CarDto;
import ua.kpi.mobiledev.domain.dto.DriverLicenseDto;
import ua.kpi.mobiledev.domain.dto.MobileNumberDto;
import ua.kpi.mobiledev.domain.dto.UserDto;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;

@Component("userConverter")
public class UserConverter<T extends User> implements CustomConverter<T, UserDto> {

    @Resource(name = "carConverter")
    private CustomConverter<Car, CarDto> carConverter;

    @Resource(name = "driverLicenseConverter")
    private CustomConverter<DriverLicense, DriverLicenseDto> driverLicenseConverter;

    @Resource(name = "mobileNumberConverter")
    private CustomConverter<MobileNumber, MobileNumberDto> mobileNumberConverter;

    @Override
    public void convert(T source, UserDto target) {
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setUserType(source.getUserType());
        target.setMobileNumbers(mapMobileNumbersToDtos(source.getMobileNumbers()));
        if (userIsDriver(source)) {
            TaxiDriver driver = (TaxiDriver) source;
            target.setCar(mapCarToDto(driver.getCar()));
            target.setDriverLicense(mapDriverLicenceToDto(driver.getDriverLicense()));
        }
    }

    private List<MobileNumberDto> mapMobileNumbersToDtos(List<MobileNumber> mobileNumbers) {
        return mobileNumbers.stream().map(this::mapMobileNumberToDto).collect(Collectors.toList());
    }

    private MobileNumberDto mapMobileNumberToDto(MobileNumber mobileNumber) {
        MobileNumberDto mobileNumberDto = new MobileNumberDto();
        mobileNumberConverter.convert(mobileNumber, mobileNumberDto);
        return mobileNumberDto;
    }

    private CarDto mapCarToDto(Car car) {
        CarDto carDto = new CarDto();
        carConverter.convert(car, carDto);
        return carDto;
    }

    private DriverLicenseDto mapDriverLicenceToDto(DriverLicense driverLicense) {
        DriverLicenseDto driverLicenseDto = new DriverLicenseDto();
        driverLicenseConverter.convert(driverLicense, driverLicenseDto);
        return driverLicenseDto;
    }

    @Override
    public void reverseConvert(UserDto source, T target) {
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setUserType(source.getUserType());
        target.setMobileNumbers(convertToMobileNumbers(ofNullable(source.getMobileNumbers()).orElse(emptyList())));
        if (userIsDriver(target)) {
            TaxiDriver driver = (TaxiDriver) target;
            driver.setCar(reverseCarConvert(source.getCar()));
            driver.setDriverLicense(reverseDriverLicenseConvert(source.getDriverLicense()));
        }
    }

    private boolean userIsDriver(User target) {
        return target.getUserType() == User.UserType.TAXI_DRIVER;
    }

    private List<MobileNumber> convertToMobileNumbers(List<MobileNumberDto> mobileNumbers) {
        return mobileNumbers.stream().map(this::convertToMobileNumber).collect(Collectors.toList());
    }

    private MobileNumber convertToMobileNumber(MobileNumberDto mobileNumberDto) {
        MobileNumber result = new MobileNumber();
        mobileNumberConverter.reverseConvert(mobileNumberDto, result);
        return result;
    }

    private Car reverseCarConvert(CarDto carDto) {
        if (isNull(carDto)) {
            return null;
        }
        Car car = new Car();
        carConverter.reverseConvert(carDto, car);
        return car;
    }

    private DriverLicense reverseDriverLicenseConvert(DriverLicenseDto driverLicense) {
        if (isNull(driverLicense)) {
            return null;
        }
        DriverLicense license = new DriverLicense();
        driverLicenseConverter.reverseConvert(driverLicense, license);
        return license;
    }
}
