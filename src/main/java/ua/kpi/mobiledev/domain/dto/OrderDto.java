package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeSerializer;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    private Long orderId;
    private Integer customerId;
    private Integer driverId;
    private Order.OrderStatus status;
    private List<AddReqSimpleDto> additionalRequirements;
    private List<RoutePointDto> routePoint;
    private String comment;
    private Double price;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
//    @NotNull(message = "startTime.required") //TODO: uncomment and add custom validation tag
//    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime startTime;
}
