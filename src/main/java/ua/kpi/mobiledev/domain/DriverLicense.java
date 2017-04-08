package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.Arrays;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "driver_license")
public class DriverLicense {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "driver_license_id")
    private Integer id;

    @Column(name = "driver_license")
    private String driverLicense;

    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;

    @Lob
    @Column(name = "front_side_scan")
    @Basic(fetch = LAZY)
    private byte[] frontSideScan;

    @Lob
    @Column(name = "back_side_scan")
    @Basic(fetch = LAZY)
    private byte[] backSideScan;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DriverLicense that = (DriverLicense) o;

        if (driverLicense != null ? !driverLicense.equals(that.driverLicense) : that.driverLicense != null)
            return false;
        if (expirationTime != null ? !expirationTime.equals(that.expirationTime) : that.expirationTime != null)
            return false;
        if (!Arrays.equals(frontSideScan, that.frontSideScan)) return false;
        return Arrays.equals(backSideScan, that.backSideScan);
    }

    @Override
    public int hashCode() {
        int result = driverLicense != null ? driverLicense.hashCode() : 0;
        result = 31 * result + (expirationTime != null ? expirationTime.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(frontSideScan);
        result = 31 * result + Arrays.hashCode(backSideScan);
        return result;
    }
}
