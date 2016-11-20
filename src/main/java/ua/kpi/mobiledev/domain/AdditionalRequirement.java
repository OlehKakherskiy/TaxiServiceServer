package ua.kpi.mobiledev.domain;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import javax.persistence.*;
import java.util.Map;

@Data
@Setter(AccessLevel.NONE)
@Entity
@Table(name = "additionalRequirement")
public abstract class AdditionalRequirement {

    @Id
    @Column(name = "idAdditionalRequirement")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Transient
    private String requirementName;

    @Transient
    private String priceDescription;

    @Transient
    private Map<Integer, String> requirementValues;

    public abstract double addPrice(Double basicPrice, Integer requirementValue);
}