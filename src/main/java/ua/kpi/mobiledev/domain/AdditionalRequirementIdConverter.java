package ua.kpi.mobiledev.domain;

import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.List;

/**
 * Created by Oleg on 05.11.2016.
 */
@Converter
public class AdditionalRequirementIdConverter implements AttributeConverter<AdditionalRequirement, Integer> {

    @Autowired
    List<AdditionalRequirement> additionalRequirementList;

    @Override
    public Integer convertToDatabaseColumn(AdditionalRequirement attribute) {
        return attribute.getId();
    }

    @Override
    public AdditionalRequirement convertToEntityAttribute(Integer dbData) {
        return additionalRequirementList.stream().filter(req -> req.getId().equals(dbData)).findFirst().orElse(null);
    }
}
