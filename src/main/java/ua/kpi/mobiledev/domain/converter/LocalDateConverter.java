package ua.kpi.mobiledev.domain.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

@Converter(autoApply = true)
public class LocalDateConverter implements AttributeConverter<LocalDate, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDate attribute) {
        return Objects.nonNull(attribute) ? Timestamp.valueOf(LocalDateTime.of(attribute, LocalTime.MIN)) : null;
    }

    @Override
    public LocalDate convertToEntityAttribute(Timestamp dbData) {
        return Objects.nonNull(dbData) ? dbData.toLocalDateTime().toLocalDate() : null;
    }
}
