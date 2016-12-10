package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "AdditionalRequirement")
@Table(name = "AdditionalRequirement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalRequirementValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idAdditionalRequirement")
    @Convert(attributeName = "idAdditionalRequirement", converter = AdditionalRequirementIdConverter.class)
    private AdditionalRequirement additionalRequirement;

    @Column(name = "idAdditionalRequirementValue")
    private Integer requirementValue;

}
