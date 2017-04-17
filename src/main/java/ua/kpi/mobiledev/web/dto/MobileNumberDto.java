package ua.kpi.mobiledev.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MobileNumberDto {

    private Integer idMobileNumber;

    @NotNull(message = "mobileNumber.required", groups = UserDto.AddUserCheck.class)
    @Pattern(regexp = "^(\\+38)*\\d{10}$", message = "mobileNumber.format", groups = {UserDto.AddUserCheck.class, UserDto.UpdateUserCheck.class})
    private String mobileNumber;
}
