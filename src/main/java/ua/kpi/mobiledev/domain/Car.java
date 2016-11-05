package ua.kpi.mobiledev.domain;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by Oleg on 05.11.2016.
 */
@Data
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "model")
    private String model;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "plateNumber")
    private String plateNumber;

    @Column(name = "seatNumber")
    private Integer seatNumber;

    private enum CarType {

        TRUCK,

        PASSENGER_CAR,

        MINIBUS
    }

    @Column(name = "carType")
    @Enumerated(EnumType.ORDINAL)
    private CarType carType;
}
