package ua.kpi.mobiledev.web.validators;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class OrderPriceDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return OrderPriceDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        OrderPriceDto orderPriceDto = (OrderPriceDto) target;
        checkDistance(orderPriceDto, errors);
        Map<Integer, Integer> requirementValueMap = orderPriceDto.paramsToMap();
        checkSetForNegativeValuesOrZero(requirementValueMap.keySet(), errors, "additionalRequirements.illegalKey");
        checkSetForNegativeValuesOrZero(new HashSet<>(requirementValueMap.values()), errors, "additionalRequirements.illegalValue");

    }

    private void checkDistance(OrderPriceDto orderPriceDto, Errors errors) {
        if (Objects.nonNull(orderPriceDto.getDistance()) && orderPriceDto.getDistance() <= 0.0) {
            errors.rejectValue("distance", "distance.illegalValue");
        }
    }

    private void checkSetForNegativeValuesOrZero(Set<Integer> values, Errors errors, String errorKey) {
        values.stream().filter(value -> value < 0).findFirst().ifPresent(value -> {
            errors.rejectValue("additionalRequirements", errorKey);
        });
    }
}
