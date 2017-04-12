package ua.kpi.mobiledev.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ua.kpi.mobiledev.domain.Car.CarType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
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

    private List<RoutePoint> routePoints;

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

    public void fillDefaultAdditionalParameters(){
        carType = Car.CarType.PASSENGER_CAR;
        paymentMethod = Order.PaymentMethod.CASH;
        withPet = false;
        withLuggage = false;
        extraPrice = 0.0;
        driveMyCar = false;
        passengerCount = 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (orderId != null ? !orderId.equals(order.orderId) : order.orderId != null) return false;
        if (!customer.equals(order.customer)) return false;
        if (taxiDriver != null ? !taxiDriver.equals(order.taxiDriver) : order.taxiDriver != null) return false;
        if (startTime != null ? !startTime.equals(order.startTime) : order.startTime != null) return false;
        if (addTime != null ? !addTime.equals(order.addTime) : order.addTime != null) return false;
        if (!distance.equals(order.distance)) return false;
        if (!price.equals(order.price)) return false;
        if (!extraPrice.equals(order.extraPrice)) return false;
        if (!withPet.equals(order.withPet)) return false;
        if (!withLuggage.equals(order.withLuggage)) return false;
        if (!driveMyCar.equals(order.driveMyCar)) return false;
        if (!passengerCount.equals(order.passengerCount)) return false;
        if (!comment.equals(order.comment)) return false;
        if (orderStatus != order.orderStatus) return false;
        if (paymentMethod != order.paymentMethod) return false;
        if (carType != order.carType) return false;
        return routePoints.equals(order.routePoints);
    }

    @Override
    public int hashCode() {
        int result = orderId != null ? orderId.hashCode() : 0;
        result = 31 * result + customer.hashCode();
        result = 31 * result + (taxiDriver != null ? taxiDriver.hashCode() : 0);
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (addTime != null ? addTime.hashCode() : 0);
        result = 31 * result + distance.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + extraPrice.hashCode();
        result = 31 * result + withPet.hashCode();
        result = 31 * result + withLuggage.hashCode();
        result = 31 * result + driveMyCar.hashCode();
        result = 31 * result + passengerCount.hashCode();
        result = 31 * result + comment.hashCode();
        result = 31 * result + orderStatus.hashCode();
        result = 31 * result + paymentMethod.hashCode();
        result = 31 * result + carType.hashCode();
        result = 31 * result + routePoints.hashCode();
        return result;
    }
}
