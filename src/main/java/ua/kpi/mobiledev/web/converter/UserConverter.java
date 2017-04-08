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
import java.util.Set;
import java.util.stream.Collectors;

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

    }

    @Override
    public void reverseConvert(UserDto source, T target) {
        target.setName(source.getName());
        target.setEmail(source.getEmail());
        target.setUserType(source.getUserType());
        target.setMobileNumbers(convertToMobileNumbers(source.getMobileNumbers()));
        if (userIsDriver(target)) {
            TaxiDriver driver = (TaxiDriver) target;
            driver.setCar(convertCar(source.getCar()));
            driver.setDriverLicense(convertDriverLicense(source.getDriverLicense()));
        }
    }

    private boolean userIsDriver(User target) {
        return target.getUserType() == User.UserType.TAXI_DRIVER;
    }

    private Set<MobileNumber> convertToMobileNumbers(List<MobileNumberDto> mobileNumbers) {
        return mobileNumbers.stream().map(this::convertToMobileNumber).collect(Collectors.toSet());
    }

    private MobileNumber convertToMobileNumber(MobileNumberDto mobileNumberDto) {
        MobileNumber result = new MobileNumber();
        mobileNumberConverter.reverseConvert(mobileNumberDto, result);
        return result;
    }

    private Car convertCar(CarDto carDto) {
        Car car = new Car();
        carConverter.reverseConvert(carDto, car);
        return car;
    }

    private DriverLicense convertDriverLicense(DriverLicenseDto driverLicense) {
        DriverLicense license = new DriverLicense();
        driverLicenseConverter.reverseConvert(driverLicense, license);
        return license;
    }
}
