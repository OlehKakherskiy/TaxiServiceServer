package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.dto.RoutePointDto;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component("orderConverter")
public class OrderConverter implements CustomConverter<OrderDto, Order> {

    @Resource(name = "routePointConverter")
    private CustomConverter<RoutePointDto, RoutePoint> routePointConverter;

    @Resource(name = "orderPriceConverter")
    private CustomConverter<OrderPriceDto, Order> orderPriceConverter;

    @Override
    public void convert(OrderDto orderDto, Order order) {
        order.setStartTime(orderDto.getStartTime());
        order.setRoutePoints(convertRoutePoints(orderDto.getRoutePoint()));
        orderPriceConverter.convert(orderDto.getOrderPrice(), order);
    }

    private List<RoutePoint> convertRoutePoints(List<RoutePointDto> routePointDtoList) {
        List<RoutePoint> routePoints = new ArrayList<>();
        int routePointIndex = 0;

        for (RoutePointDto routePointDto : routePointDtoList) {
            routePoints.add(convertRoutePoint(routePointDto, routePointIndex));
            routePointIndex++;
        }

        return routePoints;
    }

    private RoutePoint convertRoutePoint(RoutePointDto routePointDto, int routePointIndex) {
        RoutePoint routePoint = new RoutePoint();
        routePoint.setRoutePointPosition(routePointIndex);
        routePointConverter.convert(routePointDto, routePoint);
        return routePoint;
    }

    @Override
    public void reverseConvert(Order target, OrderDto source) {

    }
}
