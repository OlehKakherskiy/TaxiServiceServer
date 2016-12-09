package ua.kpi.mobiledev.repository;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

@Converter(autoApply = true)
public class LocalDateTimeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime attribute) {
        return Objects.nonNull(attribute) ? Timestamp.valueOf(attribute) : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp dbData) {
        return Objects.nonNull(dbData) ? dbData.toLocalDateTime() : null;
    }
}
