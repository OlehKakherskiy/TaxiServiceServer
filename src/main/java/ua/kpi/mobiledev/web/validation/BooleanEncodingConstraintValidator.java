package ua.kpi.mobiledev.web.validation;

import ua.kpi.mobiledev.domain.dto.AddReqSimpleDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import static java.util.Objects.isNull;

public class BooleanEncodingConstraintValidator implements ConstraintValidator<BooleanEncoding, AddReqSimpleDto> {

    private Integer addRequirementId;

    @Override
    public void initialize(BooleanEncoding constraintAnnotation) {
        addRequirementId = constraintAnnotation.requirementId();
    }

    @Override
    public boolean isValid(AddReqSimpleDto value, ConstraintValidatorContext context) {
        Integer valueId = value.getReqValueId();

        if (isNull(value) || requirementWithNotTargetId(value) || isNull(valueId)) {
            return true;
        }

        return (valueId == 0 || valueId == 1);
    }

    private boolean requirementWithNotTargetId(AddReqSimpleDto value) {
        return !addRequirementId.equals(value.getReqId());
    }
}
