package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import ua.kpi.mobiledev.domain.Order;

/**
 * Created by Oleg on 06.11.2016.
 */
@Getter
@AllArgsConstructor
public class OrderStatusDto {

    @NonNull
    private Integer userId;

    @NonNull
    private Order.OrderStatus orderStatus;

}
