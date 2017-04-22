package ua.kpi.mobiledev.web.converter;

import lombok.Setter;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.web.dto.OrderPriceDto;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import javax.annotation.Resource;

import static java.util.stream.Collectors.toList;

@Component("orderPricePopulator")
@Setter
public class OrderPricePopulator implements CustomConverter<OrderPriceDto, Order> {

    @Resource(name = "routePointConverter")
    private RoutePointConverter routePointConverter;

    @Resource(name = "additionalRequirementsConverter")
    private AdditionalRequirementsConverter additionalRequirementsConverter;

    @Override
    public void convert(OrderPriceDto source, Order target) {
        target.setRoutePoints(source.getRoutePoints().stream().map(this::convertRoutePoint).collect(toList()));
        additionalRequirementsConverter.convert(source.getAdditionalRequirements(), target);
    }

    private RoutePoint convertRoutePoint(RoutePointDto routePointDto) {
        RoutePoint routePoint = new RoutePoint();
        routePointConverter.convert(routePointDto, routePoint);
        return routePoint;
    }

    @Override
    public void reverseConvert(Order source, OrderPriceDto target) {
        target.setDistance(source.getDistance());
        target.setDuration(source.getDuration());
        target.setPrice(source.getPrice());
    }
}
