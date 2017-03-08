package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.exception.SystemException;

import java.io.IOException;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class DriverLicenseDto {

    private String driverLicenseId;
    private LocalDateTime expirationTime;
    private MultipartFile frontSideScan;
    private MultipartFile backSideScan;

    public static DriverLicense toDriverLicense(DriverLicenseDto driverLicenseDto) {
        try {
            return new DriverLicense(null, driverLicenseDto.driverLicenseId, driverLicenseDto.expirationTime, driverLicenseDto.frontSideScan.getBytes(), driverLicenseDto.backSideScan.getBytes());
        } catch (IOException e) {
            throw new SystemException("System exception during driver license scan processing");
        }
    }
}
