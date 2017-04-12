package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.domain.User;

public interface OrderStatusManager {
    Order changeOrderStatus(Order order, User user, OrderStatus changeTo);
}
