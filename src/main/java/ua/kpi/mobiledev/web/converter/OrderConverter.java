package ua.kpi.mobiledev.web.converter;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.web.dto.AddReqSimpleDto;
import ua.kpi.mobiledev.web.dto.OrderDto;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

@Component("orderConverter")
public class OrderConverter implements CustomConverter<OrderDto, Order> {

    @Resource(name = "routePointConverter")
    private CustomConverter<RoutePointDto, RoutePoint> routePointConverter;

    @Resource(name = "additionalRequirementsConverter")
    private CustomConverter<List<AddReqSimpleDto>, Order> additionalRequirementsConverter;

    @Override
    public void convert(OrderDto source, Order target) {
        target.setStartTime(source.getStartTime());
        target.setRoutePoints(convertRoutePoints(source.getRoutePoints()));
        target.setComment(source.getComment());
        additionalRequirementsConverter.convert(source.getAdditionalRequirements(), target);
    }

    private List<RoutePoint> convertRoutePoints(List<RoutePointDto> routePointDtoList) {
        return routePointDtoList.stream().map(this::convertRoutePoint).collect(Collectors.toList());
    }

    private RoutePoint convertRoutePoint(RoutePointDto routePointDto) {
        RoutePoint routePoint = new RoutePoint();
        routePointConverter.convert(routePointDto, routePoint);
        return routePoint;
    }

    @Override
    public void reverseConvert(Order source, OrderDto target) {
        target.setOrderId(source.getOrderId());
        target.setCustomerId(source.getCustomer().getId());
        TaxiDriver driver = source.getTaxiDriver();
        target.setDriverId(isNull(driver) ? null : driver.getId());
        target.setStatus(source.getOrderStatus());
        target.setStartTime(source.getStartTime());
        target.setComment(source.getComment());
        target.setPrice(source.getPrice());
        target.setDistance(source.getDistance());
        target.setExtraPrice(source.getExtraPrice());
        target.setDuration(source.getDuration());
        convertRoutePoints(source, target);
        convertAdditionalRequirements(source, target);
    }

    private void convertRoutePoints(Order source, OrderDto target) {
        List<RoutePointDto> routePointDtos = source.getRoutePoints().stream()
                .sorted(Comparator.comparing(RoutePoint::getRoutePointPosition))
                .map(this::convertRoutePoint)
                .collect(Collectors.toList());
        target.setRoutePoints(routePointDtos);
    }

    private RoutePointDto convertRoutePoint(RoutePoint routePoint) {
        RoutePointDto routePointDto = new RoutePointDto();
        routePointConverter.reverseConvert(routePoint, routePointDto);
        return routePointDto;
    }

    private void convertAdditionalRequirements(Order source, OrderDto target) {
        List<AddReqSimpleDto> additionalRequirements = new ArrayList<>();
        additionalRequirementsConverter.reverseConvert(source, additionalRequirements);
        target.setAdditionalRequirements(additionalRequirements);
    }
}
