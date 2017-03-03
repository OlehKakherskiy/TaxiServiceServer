package ua.kpi.mobiledev.domain.converter;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class GrantedAuthorityConverter implements AttributeConverter<GrantedAuthority, String> {


    @Override
    public String convertToDatabaseColumn(GrantedAuthority attribute) {
        return attribute.getAuthority();
    }

    @Override
    public GrantedAuthority convertToEntityAttribute(String dbData) {
        return new SimpleGrantedAuthority(dbData);
    }
}
