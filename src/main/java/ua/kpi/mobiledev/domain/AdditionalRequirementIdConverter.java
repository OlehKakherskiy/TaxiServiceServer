package ua.kpi.mobiledev.domain;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Map;
import java.util.Optional;

@Converter(autoApply = true)
public class AdditionalRequirementIdConverter implements AttributeConverter<AdditionalRequirement, Integer>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public Integer convertToDatabaseColumn(AdditionalRequirement attribute) {
        return attribute.getId();
    }

    @Override
    public AdditionalRequirement convertToEntityAttribute(Integer dbData) {
        Map<Integer, AdditionalRequirement> additionalRequirements = applicationContext.getBean("additionalRequirements", Map.class);
        return Optional.of(additionalRequirements.get(dbData)).orElseThrow(RuntimeException::new);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
