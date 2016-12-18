package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.MobileNumber;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileNumberDto {

    public static MobileNumberDto from(MobileNumber mobileNumber) {
        return new MobileNumberDto(mobileNumber.getIdMobileNumber(), mobileNumber.getMobileNumber());
    }

    public static MobileNumber from(MobileNumberDto mobileNumberDto) {
        return new MobileNumber(mobileNumberDto.getIdMobileNumber(), mobileNumberDto.getMobileNumber());
    }

    @NotNull(message = "mobileNumberId.required")
    @Min(value = 1, message = "mobileNumberId.negativeOrZero")
    private Integer idMobileNumber;

    @NotNull(message = "mobileNumber.required")
    private String mobileNumber;
}
