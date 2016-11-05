package ua.kpi.mobiledev.domain;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by Oleg on 05.11.2016.
 */
@Data
@Entity
@Table(name = "Account")
public class Order {

    @Id
    @Column(name = "idAccount")
    private Long orderId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idCustomer", nullable = false)
    private User customer;

    @ManyToOne(optional = false)
    @JoinColumn(name = "idTaxiDriver", nullable = true)
    private TaxiDriver taxiDriver;

    private LocalDateTime startTime;

    @Column(name = "startPoint")
    private String startPoint;

    @Column(name = "endPoint")
    private String endPoint;

    @Column(name = "price")
    private Double price;

    private enum OrderStatus {

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
    @CollectionTable(name = "accountadditionalrequirements")
    @MapKeyJoinColumn(name = "idAdditionalRequirement")
    @JoinColumn(name = "idAccount")
    @Column(name = "idAdditionalRequirementValue")
    @Convert(converter = AdditionalRequirementIdConverter.class)
    private Map<AdditionalRequirement, Integer> additionalRequirementList;

}
