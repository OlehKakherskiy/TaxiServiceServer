package ua.kpi.mobiledev.domain.orderStatusManagement;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class OrderStatusTransitionManager implements OrderStatusManager {

    private static Map<User.UserType, Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>>> defaultTransitionConfig;

    static {
        Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>> customerTransitions = new HashMap<>();
        Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>> taxiDriverTransitions = new HashMap<>();
        defaultTransitionConfig = new HashMap<>();

        defaultTransitionConfig.put(User.UserType.CUSTOMER, customerTransitions);
        defaultTransitionConfig.put(User.UserType.TAXI_DRIVER, taxiDriverTransitions);

        Map<Order.OrderStatus, OrderStatusTransition> customerFromNewTransitions = new HashMap<>();
        customerFromNewTransitions.put(Order.OrderStatus.CANCELLED, new CloseOrder());
        Map<Order.OrderStatus, OrderStatusTransition> customerFromAcceptedTransitions = new HashMap<>();
        customerFromAcceptedTransitions.put(Order.OrderStatus.CANCELLED, new CloseOrder());

        customerTransitions.put(Order.OrderStatus.NEW, customerFromNewTransitions);
        customerTransitions.put(Order.OrderStatus.ACCEPTED, customerFromAcceptedTransitions);

        Map<Order.OrderStatus, OrderStatusTransition> taxiDriverFromNewTransitions = new HashMap<>();
        taxiDriverFromNewTransitions.put(Order.OrderStatus.ACCEPTED, new AcceptOrderServicing());

        Map<Order.OrderStatus, OrderStatusTransition> taxiDriverFromAcceptedTransitions = new HashMap<>();
        taxiDriverFromAcceptedTransitions.put(Order.OrderStatus.DONE, new MarkOrderAsDone());
        taxiDriverFromAcceptedTransitions.put(Order.OrderStatus.NEW, new RefuseOrderServicing());

        taxiDriverTransitions.put(Order.OrderStatus.NEW, taxiDriverFromNewTransitions);
        taxiDriverTransitions.put(Order.OrderStatus.ACCEPTED, taxiDriverFromAcceptedTransitions);
    }

    private Map<User.UserType, Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>>> permittedTransitions;

    public OrderStatusTransitionManager(Map<User.UserType, Map<Order.OrderStatus, Map<Order.OrderStatus, OrderStatusTransition>>> permittedTransitions) {
        this.permittedTransitions = Objects.isNull(permittedTransitions) ? defaultTransitionConfig : permittedTransitions;
    }


    public OrderStatusTransitionManager() {
        permittedTransitions = defaultTransitionConfig;
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
