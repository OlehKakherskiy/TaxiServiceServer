package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.dto.OrderSimpleDto;

@Component("orderToSimpleOrderDtoConverter")
public class OrderToSimpleOrderDtoConverter implements CustomConverter<Order, OrderSimpleDto> {

    public static final int FIRST = 0;

    @Override
    public void convert(Order source, OrderSimpleDto target) {
        target.setOrderId(source.getOrderId());
        target.setPrice(source.getPrice());
        target.setStartTime(source.getStartTime());
        target.setStatus(source.getOrderStatus());
        target.setStartPoint(formatStartPointAddress(source));
        target.setEndPoint(formatEndPointAddress(source));
    }

    private String formatStartPointAddress(Order source) {
        return formatAddress(source.getRoutePoints().get(FIRST).getAddress());
    }

    private String formatEndPointAddress(Order source) {
        int lastPoint = source.getRoutePoints().size() - 1;
        return formatAddress(source.getRoutePoints().get(lastPoint).getAddress());
    }

    private String formatAddress(Address address) {
        return address.getStreet().getStreetName() + ", " + address.getHouseNumber();
    }

    @Override
    public void reverseConvert(OrderSimpleDto target, Order source) {

    }
}
