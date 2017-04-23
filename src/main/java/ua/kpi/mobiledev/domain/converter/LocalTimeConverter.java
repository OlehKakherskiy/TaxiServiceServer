package ua.kpi.mobiledev.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Objects;

@Converter(autoApply = true)
public class LocalTimeConverter implements AttributeConverter<LocalTime, Time> {

    @Override
    public Time convertToDatabaseColumn(LocalTime attribute) {
        return Objects.nonNull(attribute) ? Time.valueOf(attribute.toString()) : null;
    }

    @Override
    public LocalTime convertToEntityAttribute(Time dbData) {
        return Objects.nonNull(dbData) ? dbData.toLocalTime() : null;
    }
}
