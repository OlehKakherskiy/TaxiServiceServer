package ua.kpi.mobiledev.domain;

import lombok.Data;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * Created by Oleg on 05.11.2016.
 */
@Data
@Entity
@DiscriminatorValue("TaxiDriver")
public class TaxiDriver extends User {

    @OneToOne
    @JoinColumn(name = "idCar")
    private Car car;
}
