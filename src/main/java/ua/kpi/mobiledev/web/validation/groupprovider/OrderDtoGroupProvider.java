package ua.kpi.mobiledev.web.validation.groupprovider;

import org.hibernate.validator.spi.group.DefaultGroupSequenceProvider;
import ua.kpi.mobiledev.web.dto.OrderDto;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

public class OrderDtoGroupProvider implements DefaultGroupSequenceProvider<OrderDto> {
    @Override
    public List<Class<?>> getValidationGroups(OrderDto orderDto) {
        List<Class<?>> groups = new ArrayList<>();
        groups.add(OrderDto.class);
        if (nonNull(orderDto)) {
            if (nonNull(orderDto.getComment())) {
                groups.add(OrderDto.OptionalComment.class);
            }
            if (nonNull(orderDto.getStartTime())) {
                groups.add(OrderDto.OptionalStartTime.class);
            }
        }
        return groups;
    }
}
