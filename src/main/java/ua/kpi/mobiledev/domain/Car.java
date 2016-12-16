package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCar")
    private Integer carId;

    @Column(name = "model")
    @NonNull
    private String model;

    @Column(name = "manufacturer")
    @NonNull
    private String manufacturer;

    @Column(name = "plateNumber")
    @NonNull
    private String plateNumber;

    @Column(name = "seatNumber")
    @NonNull
    private Integer seatNumber;

    public enum CarType {

        TRUCK,

        PASSENGER_CAR,

        MINIBUS
    }

    @Column(name = "carType")
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private CarType carType;
}
