package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileNumberDto {

    @NotNull(message = "mobileNumberId.required")
    @Min(value = 1, message = "mobileNumberId.negativeOrZero")
    private Integer idMobileNumber;

    @NotNull(message = "mobileNumber.required")
    private String mobileNumber;
}
