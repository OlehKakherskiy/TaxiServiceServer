package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.kpi.mobiledev.domain.Order;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class OrderStatusDto {

    @NotNull(message = "userId.required")
    private Integer userId;

    @NotNull(message = "orderStatus.required")
    private Order.OrderStatus orderStatus;

}
