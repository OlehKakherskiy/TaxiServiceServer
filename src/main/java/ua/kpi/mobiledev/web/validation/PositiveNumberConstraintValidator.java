package ua.kpi.mobiledev.web.validation;

import ua.kpi.mobiledev.web.dto.AddReqSimpleDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class PositiveNumberConstraintValidator implements ConstraintValidator<PositiveNumber, AddReqSimpleDto> {

    private Integer addRequirementId;

    @Override
    public void initialize(PositiveNumber constraintAnnotation) {
        addRequirementId = constraintAnnotation.requirementId();
    }

    @Override
    public boolean isValid(AddReqSimpleDto value, ConstraintValidatorContext context) {
        if (isNull(value) || requirementWithNotTargetId(value) || isNull(value.getReqValueId())) {
            return true;
        }

        return value.getReqValueId() >= 0;
    }

    private boolean requirementWithNotTargetId(AddReqSimpleDto value) {
        return !addRequirementId.equals(value.getReqId());
    }

}
