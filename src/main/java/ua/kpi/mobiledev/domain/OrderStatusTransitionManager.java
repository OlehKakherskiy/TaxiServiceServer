package ua.kpi.mobiledev.domain;

import java.text.MessageFormat;
import java.util.Map;

/**
 * Created by Oleg on 06.11.2016.
 */
public class OrderStatusTransitionManager implements OrderStatusManager{

    private Map<User.UserType, Map<Order.OrderStatus, OrderStatusTransition>> permittedTransitions;

    public OrderStatusTransitionManager(Map<User.UserType, Map<Order.OrderStatus, OrderStatusTransition>> permittedTransitions) {
        this.permittedTransitions = permittedTransitions;
    }

    @Override
    public Order changeOrderStatus(Order order, User user, Order.OrderStatus orderStatus) {
        OrderStatusTransition orderStatusTransition = null;
        if (userHaveAccessToOrder(user, order) && (orderStatusTransition = getPossibleTransition(order.getOrderStatus(), user)) != null) {
            return orderStatusTransition.changeOrderStatus(order, user, orderStatus);
        } else {
            throw new IllegalStateException(MessageFormat.format("This type of user {0} can't change order status from {1} to {2}",
                    user.getUserType(), order.getOrderStatus(), orderStatus));
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
        return user.equals(order.getCustomer());
    }

    private boolean servicesOrder(User user, Order order) {
        return user.getUserType() == User.UserType.TAXI_DRIVER && user.equals(order.getTaxiDriver());
    }

    private boolean isPotentialOrderTaxiDriver(User user, Order order) {
        return order.getTaxiDriver() == null && user.getUserType() == User.UserType.TAXI_DRIVER;
    }

    private OrderStatusTransition getPossibleTransition(Order.OrderStatus currentOrderStatus, User user) {
        return permittedTransitions.get(user.getUserType()).get(currentOrderStatus);
    }
}
