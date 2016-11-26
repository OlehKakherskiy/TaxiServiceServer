package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Map;

@Setter(AccessLevel.NONE)
@Entity
@EqualsAndHashCode(exclude = "id")
@ToString
@Getter
@Table(name = "additionalRequirement")
public abstract class AdditionalRequirement {

    @Id
    @Column(name = "idAdditionalRequirement")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Transient
    protected String requirementName;

    @Transient
    private String priceDescription;

    @Transient
    protected Map<Integer, String> requirementValues;

    public AdditionalRequirement() {
    }

    public AdditionalRequirement(String requirementName, String priceDescription, Map<Integer, String> requirementValues) {
        this.requirementName = requirementName;
        this.priceDescription = priceDescription;
        this.requirementValues = requirementValues;
    }

    public abstract double addPrice(Double basicPrice, Integer requirementValueId);
}