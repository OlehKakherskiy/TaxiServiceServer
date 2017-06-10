package ua.kpi.mobiledev.domain.orderprocessability;

import lombok.Setter;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ua.kpi.mobiledev.exception.ErrorCode.DRIVER_HAS_ORDERS_IN_PROCESSING;

public class DriverHasNoOrderInProcessingDecisionStrategy implements OrderServiceAbilityDecisionStrategy {

    @Setter
    private OrderService orderService;

    @Override
    public boolean hasAbilityToProcessOrder(Order order, TaxiDriver driver) {
        List<Order> activeDriverOrders = getNotFinishedProcessingDriverOrders(driver.getId());
        if (!activeDriverOrders.isEmpty()) {
            throw new RequestException(DRIVER_HAS_ORDERS_IN_PROCESSING, activeDriverOrders.stream().map(Order::getOrderId).collect(Collectors.toList()));
        }
        return true;
    }

    private List<Order> getNotFinishedProcessingDriverOrders(Integer id) {
        List<Order> notFinishedProcessing = new ArrayList<>();
        notFinishedProcessing.addAll(orderService.getOrderList(Order.OrderStatus.ACCEPTED, id));
        notFinishedProcessing.addAll(orderService.getOrderList(Order.OrderStatus.WAITING, id));
        notFinishedProcessing.addAll(orderService.getOrderList(Order.OrderStatus.PROCESSING, id));
        return notFinishedProcessing;
    }
}
