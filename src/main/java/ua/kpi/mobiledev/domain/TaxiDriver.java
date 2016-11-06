package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.util.List;

/**
 * Created by Oleg on 05.11.2016.
 */
@NoArgsConstructor
@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity
@DiscriminatorValue("TaxiDriver")
public class TaxiDriver extends User {

    @OneToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "idCar")
    private Car car;

    public TaxiDriver(Integer id, String name, String email, List<MobileNumber> mobileNumbers, @NonNull Car car) {
        super(id, name, email, mobileNumbers);
        this.car = car;
    }
}
