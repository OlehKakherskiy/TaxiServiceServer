package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.kpi.mobiledev.domain.Order;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class OrderStatusDto {

    @NotNull(message = "userId.required")
    @Min(value = 0, message = "userId.negative")
    private Integer userId;

    @NotNull(message = "orderStatus.nullOrInvalid")
    private Order.OrderStatus orderStatus;

}
