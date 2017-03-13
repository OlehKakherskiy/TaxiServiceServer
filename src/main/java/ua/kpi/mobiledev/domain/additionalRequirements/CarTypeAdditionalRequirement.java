package ua.kpi.mobiledev.domain.additionalRequirements;

import lombok.AllArgsConstructor;
import ua.kpi.mobiledev.domain.AdditionalRequirement;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;

import java.text.MessageFormat;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static ua.kpi.mobiledev.domain.Car.CarType.valueOf;
import static ua.kpi.mobiledev.exception.ErrorCode.REQUIREMENT_VALUE_IS_NULL;

@AllArgsConstructor()
public class CarTypeAdditionalRequirement extends AdditionalRequirement {

    private static final String CANT_BE_NULL = "requirement value can't be null in requirement with name '{0}'";

    private static final String NO_REQUIREMENT_DEFINITION = "There's no requirement value definition in requirement '{0}' with value id = '{1}'";

    private static final String ILLEGAL_REQUIREMENT_VALUE_ID = "Illegal requirement value id (requirement name = '{0}', requirement value id = '{1}')";

    private Map<Car.CarType, Double> multiplyCoefficients;

    public CarTypeAdditionalRequirement(String requirementName, String priceDescription, Map<Integer, String> requirementValues, Map<Car.CarType, Double> multiplyCoefficients) {
        super(requirementName, priceDescription, requirementValues);
        this.multiplyCoefficients = ofNullable(multiplyCoefficients).orElse(Collections.emptyMap());
    }

    @Override
    public double addPrice(Double basicPrice, Integer requirementValue) {
        return basicPrice * getMultiplyCoefficient(requirementValue).orElse(0.0);
    }

    private Optional<Double> getMultiplyCoefficient(Integer requirementValueId) {
        if(Objects.isNull(requirementValueId)){
            throw new ResourceNotFoundException(REQUIREMENT_VALUE_IS_NULL, CANT_BE_NULL, requirementName);
        }
//        Objects.requireNonNull(requirementValueId, MessageFormat.format(CANT_BE_NULL, requirementName));
        return ofNullable(multiplyCoefficients.get(getCarType(ofTypeString(requirementValueId), requirementValueId)));
    }

    private String ofTypeString(Integer requirementValueId) {
        return Objects.requireNonNull(requirementValues.get(requirementValueId),
                MessageFormat.format(NO_REQUIREMENT_DEFINITION, requirementName, requirementValueId));
    }

    private Car.CarType getCarType(String carType, Integer requirementValueId) {
        try {
            return valueOf(carType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(MessageFormat.format(ILLEGAL_REQUIREMENT_VALUE_ID,
                    requirementName, requirementValueId), e.getCause());
        }
    }
}
