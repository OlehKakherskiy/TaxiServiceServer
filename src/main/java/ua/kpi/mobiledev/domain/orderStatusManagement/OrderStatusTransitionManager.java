package ua.kpi.mobiledev.domain.orderStatusManagement;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.Order.OrderStatus;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.exception.RequestException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.isNull;
import static ua.kpi.mobiledev.domain.Order.OrderStatus.*;
import static ua.kpi.mobiledev.domain.User.UserType.CUSTOMER;
import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;
import static ua.kpi.mobiledev.exception.ErrorCode.ILLEGAL_ORDER_STATUS_TRANSITION;
import static ua.kpi.mobiledev.exception.ErrorCode.USER_CANNOT_CHANGE_ORDER_STATUS;

@Component
public class OrderStatusTransitionManager implements OrderStatusManager {

    private static Map<User.UserType, Map<OrderStatus, Map<OrderStatus, OrderStatusTransition>>> defaultTransitionConfig;

    static {
        Map<OrderStatus, Map<OrderStatus, OrderStatusTransition>> customerTransitions = new HashMap<>();
        Map<OrderStatus, Map<OrderStatus, OrderStatusTransition>> taxiDriverTransitions = new HashMap<>();
        defaultTransitionConfig = new HashMap<>();

        defaultTransitionConfig.put(CUSTOMER, customerTransitions);
        defaultTransitionConfig.put(TAXI_DRIVER, taxiDriverTransitions);

        Map<OrderStatus, OrderStatusTransition> customerFromNewTransitions = new HashMap<>();
        customerFromNewTransitions.put(CANCELLED, new CloseOrder());
        Map<OrderStatus, OrderStatusTransition> customerFromAcceptedTransitions = new HashMap<>();
        customerFromAcceptedTransitions.put(CANCELLED, new CloseOrder());

        customerTransitions.put(NEW, customerFromNewTransitions);
        customerTransitions.put(ACCEPTED, customerFromAcceptedTransitions);
        customerTransitions.put(DONE, Collections.emptyMap());
        customerTransitions.put(CANCELLED, Collections.emptyMap());

        Map<OrderStatus, OrderStatusTransition> taxiDriverFromNewTransitions = new HashMap<>();
        taxiDriverFromNewTransitions.put(ACCEPTED, new AcceptOrderServicing());

        Map<OrderStatus, OrderStatusTransition> taxiDriverFromAcceptedTransitions = new HashMap<>();
        taxiDriverFromAcceptedTransitions.put(DONE, new MarkOrderAsDone());
        taxiDriverFromAcceptedTransitions.put(NEW, new RefuseOrderServicing());

        taxiDriverTransitions.put(NEW, taxiDriverFromNewTransitions);
        taxiDriverTransitions.put(ACCEPTED, taxiDriverFromAcceptedTransitions);
        taxiDriverTransitions.put(DONE, Collections.emptyMap());
        taxiDriverTransitions.put(CANCELLED, Collections.emptyMap());
    }

    private Map<User.UserType, Map<OrderStatus, Map<OrderStatus, OrderStatusTransition>>> permittedTransitions;

    public OrderStatusTransitionManager(Map<User.UserType, Map<OrderStatus, Map<OrderStatus, OrderStatusTransition>>> permittedTransitions) {
        this.permittedTransitions = isNull(permittedTransitions) ? defaultTransitionConfig : permittedTransitions;
    }

    public OrderStatusTransitionManager() {
        permittedTransitions = defaultTransitionConfig;
    }

    @Override
    public Order changeOrderStatus(Order order, User user, OrderStatus changeTo) {
        checkIfUserHaveAccessToOrder(user, order);
        OrderStatusTransition orderStatusTransition = getPossibleTransition(order.getOrderStatus(), changeTo, user);
        if (isNull(orderStatusTransition)) {
            throw new RequestException(ILLEGAL_ORDER_STATUS_TRANSITION, user.getUserType(),
                    order.getOrderStatus(), changeTo);
        }
        return orderStatusTransition.changeOrderStatus(order, user);
    }

    private void checkIfUserHaveAccessToOrder(User user, Order order) {
        boolean hasRights
                = isOrderOwner(user, order) || isPotentialOrderTaxiDriver(user, order) || servicesOrder(user, order);

        if (!hasRights) {
            throw new RequestException(USER_CANNOT_CHANGE_ORDER_STATUS, user.getId(), order.getOrderId());
        }
    }

    private boolean isOrderOwner(User user, Order order) {
        return user.getUserType() == CUSTOMER && user.getId().equals(order.getCustomer().getId());
    }

    private boolean servicesOrder(User user, Order order) {
        return user.getUserType() == TAXI_DRIVER && user.getId().equals(order.getTaxiDriver().getId());
    }

    private boolean isPotentialOrderTaxiDriver(User user, Order order) {
        return isNull(order.getTaxiDriver()) && user.getUserType() == TAXI_DRIVER;
    }

    private OrderStatusTransition getPossibleTransition(OrderStatus currentOrderStatus,
                                                        OrderStatus nextStatus, User user) {
        return permittedTransitions.get(user.getUserType()).get(currentOrderStatus).get(nextStatus);
    }
}
