package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ua.kpi.mobiledev.domain.Order;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class OrderStatusDto {

    @NotNull
    @Min(0)
    private Integer userId;

    @NotNull
    private Order.OrderStatus orderStatus;

}
