package ua.kpi.mobiledev.web.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.kpi.mobiledev.domain.dto.DriverLicenseDto;
import ua.kpi.mobiledev.domain.dto.OrderDto;

import java.time.LocalDateTime;
import java.util.Optional;

public class LocalDateTimeFutureValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderDto.class == clazz || DriverLicenseDto.class == clazz;
    }

    @Override
    public void validate(Object target, Errors errors) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(getStartTime((OrderDto) target).orElse(now))) {
            errors.rejectValue("startTime", "startTime.futureTimeRequired",
                    "startTime.futureTimeRequired");
        }
    }

    private Optional<LocalDateTime> getStartTime(OrderDto target) {
        return Optional.ofNullable(target.getStartTime());
    }
}
