package ua.kpi.mobiledev.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.LAZY;
import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@Setter
@DiscriminatorValue("1")
public class TaxiDriver extends User {

    @OneToOne(cascade = {PERSIST, MERGE}, fetch = LAZY)
    @JoinColumn(name = "car_id")
    private Car car;

    @OneToOne(cascade = ALL, fetch = LAZY)
    @JoinColumn(name = "driver_license_id")
    private DriverLicense driverLicense;

    public TaxiDriver(Integer id, String name, String email,
                      List<MobileNumber> mobileNumbers, Car car, DriverLicense driverLicense) {
        super(id, name, email, TAXI_DRIVER, mobileNumbers);
        this.car = car;
        this.driverLicense = driverLicense;
    }
}
