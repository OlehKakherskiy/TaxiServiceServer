package ua.kpi.mobiledev.web.converter;

import org.springframework.core.convert.converter.Converter;

public class StringToIntegerConverter implements Converter<String, Integer> {

    @Override
    public Integer convert(String source) {
        try {
            return Integer.valueOf(source);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
