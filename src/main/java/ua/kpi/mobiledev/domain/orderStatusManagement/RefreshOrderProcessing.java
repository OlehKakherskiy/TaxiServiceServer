package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

import java.time.LocalDateTime;

public class RefreshOrderProcessing implements OrderStatusTransition {

    @Override
    public Order changeOrderStatus(Order order, User user) {
        LocalDateTime now = LocalDateTime.now();
        order.setOrderStatus(Order.OrderStatus.NEW);
        order.setStartTime(now.toLocalDate().atStartOfDay());
        order.setAddTime(now);
        return order;
    }
}
