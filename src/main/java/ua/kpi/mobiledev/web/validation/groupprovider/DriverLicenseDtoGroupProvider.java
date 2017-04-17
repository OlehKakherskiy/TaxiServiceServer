package ua.kpi.mobiledev.web.validation.groupprovider;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import ua.kpi.mobiledev.web.dto.DriverLicenseDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class DriverLicenseDtoGroupProvider implements DefaultGroupSequenceProvider<DriverLicenseDto> {

    @Override
    public List<Class<?>> getValidationGroups(DriverLicenseDto driverLicenseDto) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(DriverLicenseDto.class);
        if (nonNull(driverLicenseDto)) {
            if (nonNull(driverLicenseDto.getCode())) {
                groups.add(DriverLicenseDto.DriverLicenseCodeCheck.class);
            }
            if (nonNull(driverLicenseDto.getExpirationTime())) {
                groups.add(DriverLicenseDto.DriverLicenseExpirationTimeCheck.class);
            }
        }
        return groups;
    }
}
