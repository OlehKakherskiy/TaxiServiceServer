package ua.kpi.mobiledev.domain.orderStatusManagement;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Oleg on 06.11.2016.
 */
public class OrderStatusTransitionManager implements OrderStatusManager {

    private Map<User.UserType, Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>>> permittedTransitions;

    public OrderStatusTransitionManager(Map<User.UserType, Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>>> permittedTransitions) {
        this.permittedTransitions = permittedTransitions;
    }

    @Override
    public Order changeOrderStatus(Order order, User user, Order.OrderStatus changeTo) {
        OrderStatusTransition orderStatusTransition = null;
        if (userHaveAccessToOrder(user, order) && Objects.nonNull(orderStatusTransition = getPossibleTransition(order.getOrderStatus(), changeTo, user))) {
            return orderStatusTransition.changeOrderStatus(order, user);
        } else {
            throw new IllegalStateException(MessageFormat.format("This type of user {0} can't change order status from {1} to {2}",
                    user.getUserType(), order.getOrderStatus(), changeTo));
        }
    }

    private boolean userHaveAccessToOrder(User user, Order order) {
        boolean hasRights = isOrderOwner(user, order) || servicesOrder(user, order) || isPotentialOrderTaxiDriver(user, order);
        if (!hasRights) {
            throw new IllegalArgumentException(MessageFormat.format("User with id = {0} has no rights to change the status of order with id = {1}", user.getId(), order.getOrderId()));
        } else {
            return true;
        }
    }

    private boolean isOrderOwner(User user, Order order) {
        return user.getUserType() == User.UserType.CUSTOMER && user.equals(order.getCustomer());
    }

    private boolean servicesOrder(User user, Order order) {
        return user.getUserType() == User.UserType.TAXI_DRIVER && user.equals(order.getTaxiDriver());
    }

    private boolean isPotentialOrderTaxiDriver(User user, Order order) {
        return order.getTaxiDriver() == null && user.getUserType() == User.UserType.TAXI_DRIVER;
    }

    private OrderStatusTransition getPossibleTransition(Order.OrderStatus currentOrderStatus, Order.OrderStatus nextStatus, User user) {
        return permittedTransitions.get(user.getUserType()).get(currentOrderStatus).get(nextStatus);
    }
}
