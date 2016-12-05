package ua.kpi.mobiledev.web.converter;

import org.springframework.core.convert.converter.Converter;
import ua.kpi.mobiledev.domain.Order;

import java.util.Arrays;
import java.util.function.Predicate;

public class StringToOrderStatusConverter implements Converter<String, Order.OrderStatus> {

    @Override
    public Order.OrderStatus convert(String source) {
        return convertOrNull(prepareForConversion(source));
    }

    private String prepareForConversion(String source) {
        return source.toUpperCase();
    }

    private Order.OrderStatus convertOrNull(String source) {
        return Arrays.stream(Order.OrderStatus.values())
                .filter(compareNameWithSource(source))
                .findFirst().orElse(null);
    }

    private Predicate<Order.OrderStatus> compareNameWithSource(String source) {
        return orderStatus -> orderStatus.name().equals(source);
    }
}
