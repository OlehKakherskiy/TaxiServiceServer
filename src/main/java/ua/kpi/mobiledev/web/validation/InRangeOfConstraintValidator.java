package ua.kpi.mobiledev.web.validation;

import ua.kpi.mobiledev.web.dto.AddReqSimpleDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class InRangeOfConstraintValidator implements ConstraintValidator<InRangeOf, AddReqSimpleDto> {

    private Integer addRequirementId;
    private int maxValue;

    @Override
    public void initialize(InRangeOf constraintAnnotation) {
        addRequirementId = constraintAnnotation.requirementId();
        maxValue = constraintAnnotation.elementsCount();
    }

    @Override
    public boolean isValid(AddReqSimpleDto value, ConstraintValidatorContext context) {
        if (isNull(value) || requirementWithNotTargetId(value) || isNull(value.getReqValueId())) {
            return true;
        }

        return maxValue > value.getReqValueId();
    }

    private boolean requirementWithNotTargetId(AddReqSimpleDto value) {
        return !addRequirementId.equals(value.getReqId());
    }
}
