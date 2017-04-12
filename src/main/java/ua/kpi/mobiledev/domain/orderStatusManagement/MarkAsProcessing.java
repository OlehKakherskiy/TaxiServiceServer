package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

import static ua.kpi.mobiledev.domain.Order.OrderStatus.PROCESSING;

public class MarkAsProcessing implements OrderStatusTransition {

    @Override
    public Order changeOrderStatus(Order order, User user) {
        order.setOrderStatus(PROCESSING);
        return order;
    }
}
