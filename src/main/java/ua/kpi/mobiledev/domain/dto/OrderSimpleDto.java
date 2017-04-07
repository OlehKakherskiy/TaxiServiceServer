package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeSerializer;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderSimpleDto {

    private Long orderId;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime startTime;

    private String startPoint;

    private String endPoint;

    private Double price;

    private OrderStatus status;
}
