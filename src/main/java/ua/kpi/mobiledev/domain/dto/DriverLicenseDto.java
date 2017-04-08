package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class DriverLicenseDto {
    private String driverLicenseId;
    private String code;
    private LocalDateTime expirationTime;
    private MultipartFile frontSideScan;
    private MultipartFile backSideScan;
}
