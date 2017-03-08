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
    @Column(name = "car_id")
    private Integer carId;

    @Column(name = "model")
    @NonNull
    private String model;

    @Column(name = "manufacturer")
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

    @Column(name = "car_type")
    @Enumerated(EnumType.ORDINAL)
    @NonNull
    private CarType carType;
}
