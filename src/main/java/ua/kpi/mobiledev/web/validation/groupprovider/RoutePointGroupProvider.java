package ua.kpi.mobiledev.web.validation.groupprovider;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class RoutePointGroupProvider implements DefaultGroupSequenceProvider<RoutePointDto> {

    @Override
    public List<Class<?>> getValidationGroups(RoutePointDto routePointDto) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(RoutePointDto.class);
        if (nonNull(routePointDto)) {
            if (nonNull(routePointDto.getLatitude())) {
                groups.add(RoutePointDto.OptionalLatitude.class);
            }
            if (nonNull(routePointDto.getLongtitude())) {
                groups.add(RoutePointDto.OptionalLongtitude.class);
            }
        }
        return groups;
    }
}
