package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

import static ua.kpi.mobiledev.domain.Order.OrderStatus.WAITING;

public class MarkAsWaiting implements OrderStatusTransition {
    @Override
    public Order changeOrderStatus(Order order, User user) {
        order.setOrderStatus(WAITING);
        return order;
    }
}
