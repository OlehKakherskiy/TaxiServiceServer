package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
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

    @Column(name = "start_point")
    private String startPoint;

    @Column(name = "end_point")
    private String endPoint;

    @Column(name = "price")
    private Double price;

    public enum OrderStatus {

        NEW,

        ACCEPTED,

        CANCELLED,

        DONE
    }

    @Column(name = "order_status_id")
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus orderStatus;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "taxi_order_id", referencedColumnName = "taxi_order_id")
    private Set<AdditionalRequirementValue> additionalRequirements;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Order order = (Order) o;

        if (!customer.equals(order.customer)) return false;
        if (taxiDriver != null ? !taxiDriver.equals(order.taxiDriver) : order.taxiDriver != null) return false;
        if (!startTime.equals(order.startTime)) return false;
        if (!startPoint.equals(order.startPoint)) return false;
        if (!endPoint.equals(order.endPoint)) return false;
        if (!price.equals(order.price)) return false;
        if (orderStatus != order.orderStatus) return false;
        return additionalRequirements.equals(order.additionalRequirements);

    }

    @Override
    public int hashCode() {
        int result = customer.hashCode();
        result = 31 * result + (taxiDriver != null ? taxiDriver.hashCode() : 0);
        result = 31 * result + startTime.hashCode();
        result = 31 * result + startPoint.hashCode();
        result = 31 * result + endPoint.hashCode();
        result = 31 * result + price.hashCode();
        result = 31 * result + orderStatus.hashCode();
        result = 31 * result + additionalRequirements.hashCode();
        return result;
    }
}
