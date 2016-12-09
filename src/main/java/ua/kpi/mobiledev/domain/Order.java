package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by Oleg on 05.11.2016.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "Account")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idAccount")
    private Long orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCustomer", nullable = false)
    private User customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idTaxiDriver", nullable = true)
    private TaxiDriver taxiDriver;

    @Column(name = "startTime")
    private LocalDateTime startTime;

    @Column(name = "startPoint")
    private String startPoint;

    @Column(name = "endPoint")
    private String endPoint;

    @Column(name = "price")
    private Double price;

    public enum OrderStatus {

        NEW,

        ACCEPTED,

        CANCELLED,

        DONE
    }

    @Column(name = "statusId")
    @Enumerated(EnumType.ORDINAL)
    private OrderStatus orderStatus;

    //stores map AdditionalRequirement/requirementValueKey (AdditionalRequirement.requirementValues.key)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "accountAdditionalRequirements")
    @MapKeyJoinColumn(name = "idAdditionalRequirement")
    @JoinColumn(name = "idAccount")
    @Column(name = "idAdditionalRequirementValue")
    @Convert(attributeName = "key", converter = AdditionalRequirementIdConverter.class)
    private Map<AdditionalRequirement, Integer> additionalRequirementList;


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
        return additionalRequirementList.equals(order.additionalRequirementList);

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
        result = 31 * result + additionalRequirementList.hashCode();
        return result;
    }
}
