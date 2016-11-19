package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

/**
 * Created by Oleg on 07.11.2016.
 */
public class RefuseOrderServicing implements OrderStatusTransition {

    @Override
    public Order changeOrderStatus(Order order, User user) {
        order.setOrderStatus(Order.OrderStatus.NEW);
        order.setTaxiDriver(null);
        return order;
    }
}
