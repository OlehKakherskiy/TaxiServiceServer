package ua.kpi.mobiledev.service;

import org.hibernate.Hibernate;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.AdditionalRequirement;
import ua.kpi.mobiledev.domain.AdditionalRequirementValue;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusManager;
import ua.kpi.mobiledev.exception.ForbiddenOperationException;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.text.MessageFormat;
import java.util.*;

import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;
import static ua.kpi.mobiledev.domain.User.UserType.TAXI_DRIVER;
import static ua.kpi.mobiledev.exception.ErrorCode.*;

@Transactional(readOnly = true)
public class TransactionalOrderService implements OrderService {

    private OrderRepository orderRepository;
    private UserService userService;
    private Map<Integer, AdditionalRequirement> additionalRequirements;
    private OrderStatusManager orderStatusManager;
    private Integer kmPrice;

    public TransactionalOrderService(OrderRepository orderRepository, UserService userService,
                                     OrderStatusManager orderStatusManager) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.additionalRequirements = emptyMap();
        this.orderStatusManager = orderStatusManager;
    }

    @Override
    @Transactional
    public Order addOrder(OrderDto orderDto) {
        OrderPriceDto orderPriceDto = orderDto.getOrderPrice();
        User user = findUser(orderDto.getCustomerId());
        checkIfCustomer(user);
        Order order = new Order(null, user, null, orderDto.getStartTime(),
                orderDto.getStartPoint(), orderDto.getEndPoint(),
                calculatePrice(orderPriceDto),
                Order.OrderStatus.NEW,
                getAdditionalRequirementValueSet(orderPriceDto));
        return orderRepository.save(order);
    }

    private void checkIfCustomer(User user) {
        if (user.getUserType() == TAXI_DRIVER) {
            throw new ForbiddenOperationException(DRIVER_CANNOT_PERFORM_OPERATION);
        }
    }

    @Override
    public Double calculatePrice(OrderPriceDto orderPriceDto) {
        if (isNull(orderPriceDto)) {
            return 0.0;
        }
        double basicPrice = ofNullable(orderPriceDto.getDistance()).orElse(0.0) * kmPrice;
        double extraPrice = getAdditionalRequirementValueSet(orderPriceDto)
                .stream()
                .map(reqEntry -> reqEntry.getAdditionalRequirement().addPrice(basicPrice, reqEntry.getRequirementValue()))
                .reduce(Double::sum)
                .orElse(0.0);
        return basicPrice + extraPrice;
    }

    private Set<AdditionalRequirementValue> getAdditionalRequirementValueSet(OrderPriceDto orderPriceDto) {
        if (isNull(orderPriceDto)) {
            return emptySet();
        }
        Map<Integer, Integer> orderRequirements = orderPriceDto.paramsToMap();
        if (isNull(orderRequirements) || orderRequirements.isEmpty()) {
            return emptySet();
        }
        Set<AdditionalRequirementValue> result = new HashSet<>();
        for (Map.Entry<Integer, Integer> orderRequirement : orderRequirements.entrySet()) {
            AdditionalRequirement additionalRequirement = convertToRequirement(orderRequirement.getKey());
            if (isValidRequirementValueId(additionalRequirement, orderRequirement.getValue())) {
                result.add(new AdditionalRequirementValue(null, additionalRequirement, orderRequirement.getValue()));
            }
        }
        return result;

    }

    private AdditionalRequirement convertToRequirement(Integer requirementId) {
        AdditionalRequirement result = additionalRequirements.get(requirementId);
        if (isNull(result)) {
            throw new ResourceNotFoundException(INVALID_REQUIREMENT_ID, requirementId);
        }
        return result;
    }

    private boolean isValidRequirementValueId(AdditionalRequirement additionalRequirement, Integer reqValueId) {
        requireNonNull(additionalRequirement.getRequirementValues().get(reqValueId),
                MessageFormat.format("There''s no value id in additional requirement with id={0}. Value id={1}",
                        additionalRequirement.getId(), reqValueId));
        return true;
    }

    @Override
    @Transactional
    public Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus) {
        Order updatedOrder = orderStatusManager.changeOrderStatus(getOrder(orderId), findUser(userId), orderStatus);
        updatedOrder = orderRepository.save(updatedOrder);
        return updatedOrder;
    }

    @Override
    public List<Order> getOrderList(Order.OrderStatus orderStatus) {
        return isNull(orderStatus) ? mapToList(orderRepository.findAll()) : orderRepository.getAllByOrderStatus(orderStatus);
    }

    private List<Order> mapToList(Iterable<Order> all) {
        List<Order> orders = new ArrayList<>();
        all.forEach(orders::add);
        return orders;
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

    @Override
    @Transactional
    public Order updateOrder(Long orderId, Integer userId, OrderDto orderDto) {
        Order order = getOrder(orderId);

        checkIfOrderOwner(order, userId);

        order.setStartTime(orderDto.getStartTime());
        order.setStartPoint(orderDto.getStartPoint());
        order.setEndPoint(orderDto.getEndPoint());
        OrderPriceDto orderPriceDto = orderDto.getOrderPrice();
        order.setPrice(calculatePrice(orderPriceDto));
        order.setAdditionalRequirements(getAdditionalRequirementValueSet(orderPriceDto));

        return orderRepository.save(order);
    }

    private void checkIfOrderOwner(Order order, Integer userId) {
        Integer customerId = order.getCustomer().getId();
        if (!customerId.equals(userId)) {
            throw new ForbiddenOperationException(USER_IS_NOT_ORDER_OWNER, userId, order.getOrderId());
        }
    }

    private User findUser(Integer userId) {
        return userService.getById(userId);
    }

    public void setKmPrice(Integer kmPrice) {
        this.kmPrice = kmPrice;
    }

    public void setAdditionalRequirements(Map<Integer, AdditionalRequirement> additionalRequirements) {
        this.additionalRequirements = additionalRequirements;
    }
}
