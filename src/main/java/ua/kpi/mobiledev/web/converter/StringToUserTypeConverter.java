package ua.kpi.mobiledev.web.converter;

import org.springframework.core.convert.converter.Converter;
import ua.kpi.mobiledev.domain.User;

public class StringToUserTypeConverter implements Converter<String, User.UserType> {

    @Override
    public User.UserType convert(String source) {
        System.out.println("source = " + source);
        return User.UserType.valueOf(source);
    }
}
