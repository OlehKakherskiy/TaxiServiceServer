package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "model")
    @NonNull
    private String model;

    @Column(name = "brand")
    @NonNull
    private String manufacturer;

    @Column(name = "plate_number")
    @NonNull
    private String plateNumber;

    @Column(name = "seat_number")
    @NonNull
    private Integer seatNumber;

    public enum CarType {

        TRUCK,

        PASSENGER_CAR,

        MINIBUS
    }

    @Column(name = "car_type_id")
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private CarType carType;
}
