package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity(name = "AdditionalRequirement")
@Table(name = "additional_requirement")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalRequirementValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "add_req_id")
    @Convert(attributeName = "add_req_id", converter = AdditionalRequirementIdConverter.class)
    private AdditionalRequirement additionalRequirement;

    @Column(name = "add_req_value_id")
    private Integer requirementValue;

}
