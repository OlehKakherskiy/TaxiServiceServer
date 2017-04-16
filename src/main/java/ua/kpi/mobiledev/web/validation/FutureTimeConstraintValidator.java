package ua.kpi.mobiledev.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.Temporal;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;

public class FutureTimeConstraintValidator implements ConstraintValidator<FutureTime, Temporal> {

    @Override
    public void initialize(FutureTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(Temporal temporal, ConstraintValidatorContext context) {
        if (isNull(temporal)) {
            return true;
        }
        if (temporal instanceof LocalDateTime) {
            return ((LocalDateTime) temporal).isAfter(now());
        }
        if (temporal instanceof LocalDate) {
            return ((LocalDate) temporal).isAfter(LocalDate.now());
        }
        return false;
    }
}
