package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;
import ua.kpi.mobiledev.web.validation.BooleanEncoding;
import ua.kpi.mobiledev.web.validation.InRangeOf;
import ua.kpi.mobiledev.web.validation.PositiveNumber;

import javax.validation.GroupSequence;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@BooleanEncoding.List({
        @BooleanEncoding(requirementId = 3, message = "additionalRequirement.idValue.withPet", groups = AddReqSimpleDto.AdditionalRequirementCheck.class),
        @BooleanEncoding(requirementId = 4, message = "additionalRequirement.idValue.withLuggage", groups = AddReqSimpleDto.AdditionalRequirementCheck.class),
        @BooleanEncoding(requirementId = 6, message = "additionalRequirement.idValue.driveMyCar", groups = AddReqSimpleDto.AdditionalRequirementCheck.class),
})
@InRangeOf.List({
        @InRangeOf(requirementId = 1, elementsCount = 3, message = "additionalRequirement.idValue.carType", groups = AddReqSimpleDto.AdditionalRequirementCheck.class),
        @InRangeOf(requirementId = 2, elementsCount = 2, message = "additionalRequirement.idValue.paymentMethod", groups = AddReqSimpleDto.AdditionalRequirementCheck.class)
})
@PositiveNumber.List({
        @PositiveNumber(requirementId = 5, message = "additionalRequirement.idValue.extraPrice", groups = AddReqSimpleDto.AdditionalRequirementCheck.class),
        @PositiveNumber(requirementId = 7, message = "additionalRequirement.idValue.passengerCount", groups = AddReqSimpleDto.AdditionalRequirementCheck.class)
})

@GroupSequence({AddReqSimpleDto.AdditionalRequirementCheck.class, AddReqSimpleDto.class})
public class AddReqSimpleDto {

    public interface BasicAdditionalRequirementCheck {
    }

    public interface AdditionalRequirementCheck extends BasicAdditionalRequirementCheck{
    }

    @NotNull(message = "additionalRequirement.id.required", groups = BasicAdditionalRequirementCheck.class)
    @Range(min = 1, max = 7, message = "additionalRequirement.id.notInRange", groups = BasicAdditionalRequirementCheck.class)
    private Integer reqId;

    @NotNull(message = "additionalRequirement.idValue.required", groups = BasicAdditionalRequirementCheck.class)
    private Integer reqValueId;
}
