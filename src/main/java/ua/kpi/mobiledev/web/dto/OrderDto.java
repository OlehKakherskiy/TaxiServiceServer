package ua.kpi.mobiledev.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import org.hibernate.validator.group.GroupSequenceProvider;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeDeserializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalDateTimeSerializer;
import ua.kpi.mobiledev.web.localDateTimeMapper.LocalTimeSerializer;
import ua.kpi.mobiledev.web.validation.FutureTime;
import ua.kpi.mobiledev.web.validation.groupprovider.OrderDtoGroupProvider;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
@GroupSequenceProvider(OrderDtoGroupProvider.class)
public class OrderDto {

    public interface AddOrderGroup extends RoutePointDto.FullRoutePointCheck,
            AddReqSimpleDto.AdditionalRequirementCheck, FutureTimeMandatoryCheck{
    }

    public interface UpdateOrderGroup extends OptionalComment, OptionalStartTime,
            RoutePointDto.OptionalLatitude, RoutePointDto.OptionalLongtitude, AddReqSimpleDto.AdditionalRequirementCheck {
    }

    public interface OptionalComment {
    }

    public interface OptionalStartTime {
    }

    public interface  FutureTimeMandatoryCheck{
    }

    private Long orderId;
    private Integer customerId;
    private Integer driverId;
    private Order.OrderStatus status;

    @Valid
    private List<AddReqSimpleDto> additionalRequirements;

    @NotNull(message = "routePoints.required", groups = AddOrderGroup.class)
    @Size(min = 2, message = "routePoints.invalidCount", groups = AddOrderGroup.class)
    @Valid
    private List<RoutePointDto> routePoints;

    @Size(max = 300, message = "orderComment.maxLength", groups = OptionalComment.class)
    private String comment;

    private Double distance;
    private Double price;
    private Double extraPrice;
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime duration;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @FutureTime(message = "startTime.notFuture", groups = {FutureTimeMandatoryCheck.class, OptionalStartTime.class})
    private LocalDateTime startTime;

    private Boolean quickRequest = false;
}
