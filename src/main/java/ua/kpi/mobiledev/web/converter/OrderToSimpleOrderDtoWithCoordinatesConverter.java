package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.web.dto.OrderSimpleDto;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import java.util.List;

@Component("simpleOrderDtoConverterWithCoordinates")
public class OrderToSimpleOrderDtoWithCoordinatesConverter implements CustomConverter<Order, OrderSimpleDto> {

    @Override
    public void convert(Order source, OrderSimpleDto target) {
        target.setOrderId(source.getOrderId());
        target.setPrice(source.getPrice());
        target.setStartTime(source.getStartTime());
        target.setStatus(source.getOrderStatus());
        List<RoutePoint> routePoint = source.getRoutePoints();
        int lastIndex = routePoint.size() - 1;
        target.setStartPointCoordinates(convertCoordinates(routePoint.get(0)));
        target.setEndPointCoordinates(convertCoordinates(routePoint.get(lastIndex)));
    }

    private RoutePointDto convertCoordinates(RoutePoint routePoint) {
        RoutePointDto routePointDto = new RoutePointDto();
        routePointDto.setLatitude(routePoint.getLatitude().toString());
        routePointDto.setLongtitude(routePoint.getLongtitude().toString());
        return routePointDto;
    }

    @Override
    public void reverseConvert(OrderSimpleDto source, Order target) {

    }
}
