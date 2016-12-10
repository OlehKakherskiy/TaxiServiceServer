package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("1")
public class TaxiDriver extends User {

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE},fetch = FetchType.LAZY)
    @JoinColumn(name = "idCar")
    private Car car;

    public TaxiDriver(Integer id, String name, String email, Set<MobileNumber> mobileNumbers, @NonNull Car car) {
        super(id, name, email, UserType.TAXI_DRIVER, mobileNumbers);
        this.car = car;
    }
}
