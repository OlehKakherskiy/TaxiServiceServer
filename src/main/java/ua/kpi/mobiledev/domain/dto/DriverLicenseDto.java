package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@NoArgsConstructor
@Setter
@Getter
public class DriverLicenseDto {
    private Integer driverLicenseId;
    private String code;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expirationTime;
    private MultipartFile frontSideScan;
    private MultipartFile backSideScan;
}
