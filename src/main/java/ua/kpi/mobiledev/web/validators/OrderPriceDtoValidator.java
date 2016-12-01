package ua.kpi.mobiledev.web.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;

import java.util.Map;
import java.util.Set;

public class OrderPriceDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderPriceDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmpty(errors, "distance", "distance.required");
        OrderPriceDto orderDto = (OrderPriceDto) target;
        if (orderDto.getDistance() <= 0.0) {
            errors.rejectValue("distance", "distance.illegalValue");
        }
        ValidationUtils.rejectIfEmpty(errors, "additionalRequirements", "additionalRequirements.required");
        Map<Integer, Integer> requirementValueMap = orderDto.paramsToMap();
        checkSetForNegativeValuesOrZero(requirementValueMap.keySet(), errors, "additionalRequirements.illegalKey");
        checkSetForNegativeValuesOrZero(requirementValueMap.keySet(), errors, "additionalRequirements.illegalValue");
    }

    private void checkSetForNegativeValuesOrZero(Set<Integer> values, Errors errors, String errorKey) {
        values.stream().filter(value -> value < 0).findFirst().ifPresent(value -> {
            errors.rejectValue("additionalRequirements", errorKey);
        });
    }
}
