package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeSerializer;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.Objects;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    public static OrderDto from(Order order) {
        if (Objects.isNull(order)) {
            return new OrderDto();
        }
        OrderDto orderDto = new OrderDto();
        orderDto.customerId = order.getCustomer().getId();
        orderDto.startTime = order.getStartTime();
        orderDto.price = order.getPrice();
        return orderDto;
    }

    @NotNull(message = "customerId.required")
    @Min(value = 0, message = "customerId.invalidValue")
    private Integer customerId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @NotNull(message = "startTime.required")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;

    @NotNull(message = "startPoint.required")
    @Size(min = 1, max = 200, message = "startPoint.invalidSize")
    private String startPoint;

    @NotNull(message = "endPoint.required")
    @Size(min = 1, max = 200, message = "endPoint.invalidSize")
    private String endPoint;

    @NotNull(message = "orderPrice.required")
    @Valid
    private OrderPriceDto orderPrice;

    private Double price;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OrderDto orderDto = (OrderDto) o;

        if (customerId != null ? !customerId.equals(orderDto.customerId) : orderDto.customerId != null) return false;
        if (startTime != null ? !startTime.equals(orderDto.startTime) : orderDto.startTime != null) return false;
        if (startPoint != null ? !startPoint.equals(orderDto.startPoint) : orderDto.startPoint != null) return false;
        if (endPoint != null ? !endPoint.equals(orderDto.endPoint) : orderDto.endPoint != null) return false;
        return orderPrice != null ? orderPrice.equals(orderDto.orderPrice) : orderDto.orderPrice == null;
    }

    @Override
    public int hashCode() {
        int result = customerId != null ? customerId.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (startPoint != null ? startPoint.hashCode() : 0);
        result = 31 * result + (endPoint != null ? endPoint.hashCode() : 0);
        result = 31 * result + (orderPrice != null ? orderPrice.hashCode() : 0);
        return result;
    }
}
