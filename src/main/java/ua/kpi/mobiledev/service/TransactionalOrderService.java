package ua.kpi.mobiledev.service;

import lombok.Setter;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusManager;
import ua.kpi.mobiledev.domain.priceCalculationManagement.PriceCalculationManager;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.repository.OrderRepository;
import ua.kpi.mobiledev.service.googlemaps.GeographicalPoint;
import ua.kpi.mobiledev.service.googlemaps.GoogleMapsClientService;
import ua.kpi.mobiledev.service.googlemaps.GoogleMapsRouteResponse;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.function.Consumer;

import static java.time.LocalDateTime.MIN;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;
import static ua.kpi.mobiledev.exception.ErrorCode.*;

@Transactional(readOnly = true)
@Setter
@Service("orderService")
public class TransactionalOrderService implements OrderService {

    private static final double TO_KILOMETERS = 1000.0;

    @Resource(name = "orderRepository")
    private OrderRepository orderRepository;

    @Resource(name = "userService")
    private UserService userService;

    @Resource(name = "orderStatusManager")
    private OrderStatusManager orderStatusManager;

    @Resource(name = "priceCalculationManager")
    private PriceCalculationManager priceCalculationManager;

    @Resource(name = "googleMapsService")
    private GoogleMapsClientService googleMapsService;

    @Resource(name = "notificationService")
    private NotificationService notificationService;

    @Override
    @Transactional
    public Order addOrder(Order order, Integer userId) {
        order.setCustomer(checkIfCustomer(findUser(userId)));
        getOrderRouteParams(order);
        for (int i = 0; i < order.getRoutePoints().size(); i++) {
            order.getRoutePoints().get(i).setRoutePointPosition(i);
        }
        order.setStartTime(ofNullable(order.getStartTime()).orElse(LocalDate.now().atStartOfDay()));
        order.setAddTime(LocalDateTime.now());
        order.setRemoved(false);
        return orderRepository.save(order);
    }

    private User checkIfCustomer(User user) {
        if (user.getUserType() == TAXI_DRIVER) {
            throw new ForbiddenOperationException(DRIVER_CANNOT_PERFORM_OPERATION);
        }
        return user;
    }

    private User findUser(Integer userId) {
        return userService.getById(userId);
    }

    @Override
    public Order getOrderRouteParams(Order notCompletedOrder) {
        if (isNull(notCompletedOrder)) {
            return new Order();
        }

        GoogleMapsRouteResponse routeParams = getDistanceAndDuration(notCompletedOrder);
        notCompletedOrder.setDistance(convertDistanceToKilometers(routeParams.getDistance()));
        notCompletedOrder.setDuration(LocalTime.ofSecondOfDay(routeParams.getDuration()));
        notCompletedOrder.setPrice(calculateOrderPrice(notCompletedOrder));

        return notCompletedOrder;
    }

    private GoogleMapsRouteResponse getDistanceAndDuration(Order order) {
        List<GeographicalPoint> geographicalPoints = order.getRoutePoints().stream()
                .map(routePoint -> new GeographicalPoint(routePoint.getLatitude(), routePoint.getLongtitude()))
                .collect(toList());
        return googleMapsService.calculateDistance(geographicalPoints);
    }

    private double convertDistanceToKilometers(Integer distanceInMeters) {
        return distanceInMeters / TO_KILOMETERS;
    }

    private double calculateOrderPrice(Order order) {
        Double orderPrice = priceCalculationManager.calculateOrderPrice(order).getPrice();
        return new BigDecimal(orderPrice).setScale(2, RoundingMode.UP).doubleValue();
    }

    @Override
    @Transactional
    public Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus) {
        User whoChange = findUser(userId);
        Order withChangedStatus = orderStatusManager.changeOrderStatus(getOrder(orderId), whoChange, orderStatus);
        orderRepository.save(withChangedStatus);
        notificationService.sendUpdateOrderNotification(withChangedStatus, whoChange);
        return withChangedStatus;
    }

    @Override
    public List<Order> getOrderList(Order.OrderStatus orderStatus, Integer userId) {
        return orderRepository.getAllByOrderStatus(orderStatus, userService.getById(userId));
    }

    @Override
    public Order getOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        if (isNull(order)) {
            throw new ResourceNotFoundException(ORDER_NOT_FOUND_WITH_ID, orderId);
        }
        Hibernate.initialize(order.getTaxiDriver());
        return order;
    }

    @Override
    @Transactional
    public void deleteOrder(Long orderId, Integer userId) {
        checkIfCustomer(userService.getById(userId));
        Order order = getOrder(orderId);
        checkIfOrderOwner(order, userId);

        orderRepository.delete(order);
    }

    private void checkIfOrderOwner(Order order, Integer userId) {
        Integer customerId = order.getCustomer().getId();
        if (!customerId.equals(userId)) {
            throw new ForbiddenOperationException(USER_IS_NOT_ORDER_OWNER, userId, order.getOrderId());
        }
    }

    @Override
    @Transactional
    public Order updateOrder(Order orderPrototype, Integer userId) {
        User customer = checkIfCustomer(findUser(userId));
        Order originalOrder = getOrder(orderPrototype.getOrderId());
        checkIfOrderOwner(originalOrder, customer.getId());

        updateStartTime(orderPrototype.getStartTime(), originalOrder);
        updateParameterIfPresent(orderPrototype.getExtraPrice(), originalOrder::setExtraPrice);
        updateParameterIfPresent(orderPrototype.getWithPet(), originalOrder::setWithPet);
        updateParameterIfPresent(orderPrototype.getWithLuggage(), originalOrder::setWithLuggage);
        updateParameterIfPresent(orderPrototype.getDriveMyCar(), originalOrder::setDriveMyCar);
        updateParameterIfPresent(orderPrototype.getPassengerCount(), originalOrder::setPassengerCount);
        updateParameterIfPresent(orderPrototype.getComment(), originalOrder::setComment);
        updateParameterIfPresent(orderPrototype.getPaymentMethod(), originalOrder::setPaymentMethod);
        updateParameterIfPresent(orderPrototype.getCarType(), originalOrder::setCarType);

        boolean routeWasUpdated = updateRoutePoints(orderPrototype.getRoutePoints(), originalOrder);
        if (routeWasUpdated) {
            GoogleMapsRouteResponse routeInfo = getDistanceAndDuration(originalOrder);
            originalOrder.setDuration(LocalTime.ofSecondOfDay(routeInfo.getDuration()));
            originalOrder.setDistance(convertDistanceToKilometers(routeInfo.getDistance()));
        }

        originalOrder.setPrice(calculateOrderPrice(originalOrder));

        return orderRepository.save(originalOrder);
    }

    private void updateStartTime(LocalDateTime startTime, Order originalOrder) {
        if (MIN == startTime) {
            originalOrder.setStartTime(LocalDate.now().atStartOfDay());
        } else if (nonNull(startTime)) {
            originalOrder.setStartTime(startTime);
        }
    }

    private <T> void updateParameterIfPresent(T param, Consumer<T> set) {
        ofNullable(param).ifPresent(set);
    }

    private boolean updateRoutePoints(List<RoutePoint> routePoints, Order originalOrder) {
        if (isNull(routePoints) || routePoints.size() == 0) {
            return false;
        }
        List<RoutePoint> originalRoutePoints = originalOrder.getRoutePoints();
        for (RoutePoint routePointPrototype : routePoints) {
            if (routePointIsNew(routePointPrototype)) {
                addNewRoutePoint(originalRoutePoints, routePointPrototype);
            } else {
                updateExistedRoutePoint(routePointPrototype, originalRoutePoints);
            }
        }
        for (int i = 0; i < originalRoutePoints.size(); i++) {
            originalRoutePoints.get(i).setRoutePointPosition(i);
        }

        return !routePoints.isEmpty();
    }

    private void addNewRoutePoint(List<RoutePoint> originalRoutePoints, RoutePoint routePointPrototype) {
        int position = processNewPosition(routePointPrototype.getRoutePointPosition(), originalRoutePoints);
        originalRoutePoints.add(position, routePointPrototype);
    }

    private int processNewPosition(Integer routePointPosition, List<RoutePoint> originalRoutePoints) {
        return isNull(routePointPosition) ? originalRoutePoints.size() : routePointPosition;
    }

    private boolean routePointIsNew(RoutePoint routePointPrototype) {
        return isNull(routePointPrototype.getRoutePointId());
    }

    private void updateExistedRoutePoint(RoutePoint routePointPrototype, List<RoutePoint> originalRoutePoints) {
        RoutePoint toUpdate = originalRoutePoints.stream()
                .filter(routePoint -> routePointPrototype.getRoutePointId().equals(routePoint.getRoutePointId()))
                .findAny()
                .orElseThrow(() -> new ResourceNotFoundException(NO_ROUTE_POINT_WITH_ID, routePointPrototype.getRoutePointId()));
        if (needRemove(routePointPrototype)) {
            originalRoutePoints.remove(toUpdate);
            return;
        }
        if (needReplacePosition(routePointPrototype, toUpdate)) {
            replacePosition(toUpdate, routePointPrototype.getRoutePointPosition(), originalRoutePoints);
        }
        tryUpdate(toUpdate, routePointPrototype);
    }

    private void tryUpdate(RoutePoint toUpdate, RoutePoint routePointPrototype) {
        if (isNull(routePointPrototype.getLatitude()) || isNull(routePointPrototype.getLongtitude())) {
            return;
        }
        if (toUpdate.getLatitude().equals(routePointPrototype.getLatitude()) &&
                toUpdate.getLongtitude().equals(routePointPrototype.getLongtitude())) {
            return;
        }
        toUpdate.setAddress(routePointPrototype.getAddress());
        toUpdate.setLatitude(routePointPrototype.getLatitude());
        toUpdate.setLongtitude(routePointPrototype.getLongtitude());
    }

    private boolean needRemove(RoutePoint routePointPrototype) {
        return isNull(routePointPrototype.getRoutePointPosition());
    }

    private boolean needReplacePosition(RoutePoint routePointPrototype, RoutePoint toUpdate) {
        return !routePointPrototype.getRoutePointPosition().equals(toUpdate.getRoutePointPosition());
    }

    private void replacePosition(RoutePoint toUpdate, int newPosition, List<RoutePoint> originalRoutePoints) {
        originalRoutePoints.remove(toUpdate);
        originalRoutePoints.add(newPosition, toUpdate);
    }
}
