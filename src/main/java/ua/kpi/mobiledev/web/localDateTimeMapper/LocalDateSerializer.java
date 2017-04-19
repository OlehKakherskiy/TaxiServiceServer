package ua.kpi.mobiledev.web.localDateTimeMapper;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateSerializer extends StdSerializer<LocalDate> {

    public LocalDateSerializer(){
        super(LocalDate.class);
    }

    @Override
    public void serialize(LocalDate localDate, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
    }
}