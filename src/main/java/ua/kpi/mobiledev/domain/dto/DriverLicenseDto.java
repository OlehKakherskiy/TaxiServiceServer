package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.group.GroupSequenceProvider;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateSerializer;
import ua.kpi.mobiledev.web.validation.FutureTime;
import ua.kpi.mobiledev.web.validation.groupprovider.DriverLicenseDtoGroupProvider;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@NoArgsConstructor
@Setter
@Getter
@GroupSequenceProvider(DriverLicenseDtoGroupProvider.class)
public class DriverLicenseDto {

    public interface DriverLicenseCheck extends DriverLicenseCodeCheck, DriverLicenseExpirationTimeCheck {
    }

    public interface DriverLicenseCodeCheck {
    }

    public interface DriverLicenseExpirationTimeCheck {
    }

    @NotNull(groups = UserDto.AddUserCheck.class, message = "driverLicense.code.required")
    @Pattern(regexp = "^[\u0410-\u042f]{3} \\d{6}$",
            groups = {UserDto.AddUserCheck.class, DriverLicenseCodeCheck.class}, message = "driverLicense.code.invalidFormat")
    private String code;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @NotNull(groups = UserDto.AddUserCheck.class, message = "driverLicense.expirationTime.required")
    @FutureTime(groups = {UserDto.AddUserCheck.class, DriverLicenseDto.DriverLicenseExpirationTimeCheck.class}, message = "driverLicense.expirationTime.notFuture")
    private LocalDate expirationTime;

    private MultipartFile frontSideScan;
    private MultipartFile backSideScan;
}
