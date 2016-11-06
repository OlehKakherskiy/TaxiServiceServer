package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

/**
 * Created by Oleg on 06.11.2016.
 */
public interface OrderStatusTransition {

    Order changeOrderStatus(Order order, User user, Order.OrderStatus orderStatus);
}
