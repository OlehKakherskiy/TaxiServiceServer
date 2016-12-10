package ua.kpi.mobiledev.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Map;

@Data
public abstract class AdditionalRequirement {

    private Integer id;

    protected String requirementName;

    private String priceDescription;

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