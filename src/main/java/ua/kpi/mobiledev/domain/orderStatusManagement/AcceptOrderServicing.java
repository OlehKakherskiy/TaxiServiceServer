package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

/**
 * Created by Oleg on 07.11.2016.
 */
public class AcceptOrderServicing implements OrderStatusTransition {

    @Override
    public Order changeOrderStatus(Order order, User user, Order.OrderStatus orderStatus) {
        order.setTaxiDriver((TaxiDriver) user);
        order.setOrderStatus(Order.OrderStatus.ACCEPTED);
        return order;
    }
}
