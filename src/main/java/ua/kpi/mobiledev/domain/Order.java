package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.Car.CarType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "taxi_order")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "taxi_order_id")
    private Long orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "taxi_driver_id")
    private TaxiDriver taxiDriver;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "add_time")
    private LocalDateTime addTime;

    @Column(name = "distance")
    private Double distance;

    @Column(name = "price")
    private Double price;

    @Column(name = "extra_price")
    private Double extraPrice;

    @Column(name = "pet")
    private Boolean withPet;

    @Column(name = "luggage")
    private Boolean withLuggage;

    @Column(name = "drive_my_car")
    private Boolean driveMyCar;

    @Column(name = "passenger_count")
    private Integer passengerCount;

    @Column(name = "comment")
    private String comment;

    @Column(name = "order_status_id")
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus orderStatus;

    @Column(name = "payment_method_id")
    @Enumerated(EnumType.ORDINAL)
    private PaymentMethod paymentMethod;

    @Column(name = "car_type_id")
    @Enumerated(EnumType.ORDINAL)
    private CarType carType;

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(name = "taxi_order_has_address",
            joinColumns = {@JoinColumn(name = "taxi_order_id", referencedColumnName = "taxi_order_id")},
            inverseJoinColumns = {@JoinColumn(name = "address_id", referencedColumnName = "address_id")}
    )
    @OrderColumn(name = "point_index")
    private Set<Address> routePoints;

    public enum OrderStatus {

        NEW,

        ACCEPTED,

        CANCELLED,

        DONE
    }

    public enum PaymentMethod {
        CASH,

        CREDIT_CARD
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!customer.equals(order.customer)) return false;
        if (taxiDriver != null ? !taxiDriver.equals(order.taxiDriver) : order.taxiDriver != null) return false;
        if (!startTime.equals(order.startTime)) return false;
        if (!price.equals(order.price)) return false;
        return orderStatus == order.orderStatus;

    }

    @Override
    public int hashCode() {
        int result = customer.hashCode();
        result = 31 * result + (taxiDriver != null ? taxiDriver.hashCode() : 0);
        result = 31 * result + startTime.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + orderStatus.hashCode();
        return result;
    }
}
