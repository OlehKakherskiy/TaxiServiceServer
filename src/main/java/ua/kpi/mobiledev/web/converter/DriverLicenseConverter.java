package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.dto.DriverLicenseDto;

@Component("driverLicenseConverter")
public class DriverLicenseConverter implements CustomConverter<DriverLicense, DriverLicenseDto> {

    @Override
    public void convert(DriverLicense source, DriverLicenseDto target) {

    }

    @Override
    public void reverseConvert(DriverLicenseDto source, DriverLicense target) {
        target.setDriverLicense(source.getCode());
        target.setExpirationTime(source.getExpirationTime());
    }
}
