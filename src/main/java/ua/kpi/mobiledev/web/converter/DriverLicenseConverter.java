package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.dto.DriverLicenseDto;

import static java.util.Objects.isNull;

@Component("driverLicenseConverter")
public class DriverLicenseConverter implements CustomConverter<DriverLicense, DriverLicenseDto> {

    @Override
    public void convert(DriverLicense source, DriverLicenseDto target) {
        target.setCode(source.getDriverLicense());
        target.setExpirationTime(source.getExpirationTime());
    }

    @Override
    public void reverseConvert(DriverLicenseDto source, DriverLicense target) {
        if(isNull(source)){
            return;
        }
        target.setDriverLicense(source.getCode());
        target.setExpirationTime(source.getExpirationTime());
    }
}
