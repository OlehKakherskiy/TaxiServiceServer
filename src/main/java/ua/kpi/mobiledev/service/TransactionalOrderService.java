package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import ua.kpi.mobiledev.domain.AdditionalRequirement;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.domain.dto.OrderPriceDto;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusManager;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.text.MessageFormat;
import java.util.*;

public class TransactionalOrderService implements OrderService {

    private OrderRepository orderRepository;

    private UserService userService;

    private Map<Integer, AdditionalRequirement> additionalRequirements;

    private OrderStatusManager orderStatusManager;

    private Integer kmPrice;

    @Autowired
    public TransactionalOrderService(OrderRepository orderRepository, UserService userService, Map<Integer, AdditionalRequirement> additionalRequirements, OrderStatusManager orderStatusManager) {
        this.orderRepository = orderRepository;
        this.userService = userService;
        this.additionalRequirements = additionalRequirements;
        this.orderStatusManager = orderStatusManager;
    }

    @Override
    public Order addOrder(OrderDto orderDto) {
        User user = findUser(orderDto.getCustomerId());
        if (user.getUserType() == User.UserType.TAXI_DRIVER) {
            throw new IllegalArgumentException("Taxi driver can't add order");
        }
        OrderPriceDto orderPriceDto = Objects.requireNonNull(orderDto.getOrderPrice(), "There's no data for order price calculation");
        Order order = new Order(null, user, null, orderDto.getStartTime(),
                orderDto.getStartPoint(), orderDto.getEndPoint(),
                calculatePrice(orderPriceDto),
                Order.OrderStatus.NEW,
                getAdditionalRequirementMap(orderPriceDto));
        return orderRepository.save(order);
    }

    @Override
    public Double calculatePrice(OrderPriceDto orderPriceDto) {
        double basicPrice = orderPriceDto.getDistance() * kmPrice;
        double extraPrice = getAdditionalRequirementMap(orderPriceDto).entrySet()
                .stream()
                .map(reqEntry -> reqEntry.getKey().addPrice(basicPrice, reqEntry.getValue()))
                .reduce(Double::sum).orElse(0.0);
        return basicPrice + extraPrice;
    }

    private Map<AdditionalRequirement, Integer> getAdditionalRequirementMap(OrderPriceDto orderPriceDto) {
        if (Objects.isNull(orderPriceDto)) {
            return Collections.emptyMap();
        }
        Map<Integer, Integer> orderRequirements = orderPriceDto.getAdditionalRequestValueMap();
        if (Objects.nonNull(orderRequirements) && !orderRequirements.isEmpty()) {
            return Collections.emptyMap();
        } else {
            Map<AdditionalRequirement, Integer> result = new HashMap<>();
            for (Map.Entry<Integer, Integer> orderRequirement : orderRequirements.entrySet()) {
                AdditionalRequirement additionalRequirement = convertToRequirement(orderRequirement.getKey());
                if (isValidRequirementValueId(additionalRequirement, orderRequirement.getValue())) {
                    result.put(additionalRequirement, orderRequirement.getValue());
                }
            }
            return result;
        }
    }

    private AdditionalRequirement convertToRequirement(Integer requirementId) {
        return Objects.requireNonNull(additionalRequirements.get(requirementId),
                MessageFormat.format("Illegal requirement id. Id = {0}", requirementId));
    }

    private boolean isValidRequirementValueId(AdditionalRequirement additionalRequirement, Integer reqValueId) {
        Objects.requireNonNull(additionalRequirement.getRequirementValues().get(reqValueId),
                MessageFormat.format("There's no value id in additional requirement with id={0}. Value id={1}",
                        additionalRequirement.getId(), reqValueId));
        return true;
    }

    @Override
    public Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus) {
        return orderStatusManager.changeOrderStatus(findOrder(orderId), findUser(userId), orderStatus);
    }

    @Override
    public List<Order> getOrderList(Order.OrderStatus orderStatus) {
        return orderRepository.getAllByOrderStatus(orderStatus);
    }

    @Override
    public Order getOrder(Long orderId) {
        return orderRepository.findOne(orderId);
    }

    @Override
    public Boolean deleteOrder(Long orderId, Integer userId) {
        Order order = getOrder(orderId);
        if (isUserOwner(order, userId)) {
            orderRepository.delete(order);
            return true;
        } else {
            return false;
        }
    }

    private boolean isUserOwner(Order order, Integer userId) {
        return Integer.compare(userId, order.getCustomer().getId()) == 0;
    }

    @Override
    public Order updateOrder(Long orderId, OrderDto orderDto) {
        Order order = findOrder(orderId);
        order.setStartTime(Objects.isNull(orderDto.getStartTime()) ? order.getStartTime() : orderDto.getStartTime());
        order.setStartPoint(Objects.isNull(orderDto.getStartPoint()) ? order.getStartPoint() : orderDto.getStartPoint());
        order.setEndPoint(Objects.isNull(orderDto.getEndPoint()) ? order.getEndPoint() : orderDto.getEndPoint());
        OrderPriceDto orderPriceDto = orderDto.getOrderPrice();
        if (!Objects.isNull(orderPriceDto)) {
            order.setPrice(calculatePrice(orderPriceDto));
            order.setAdditionalRequirementList(getAdditionalRequirementMap(orderDto.getOrderPrice()));
        }
        orderRepository.save(order);
        return order;
    }

    private User findUser(Integer userId) {
        return userService.getUser(userId);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findOne(orderId);
    }

    public void setKmPrice(Integer kmPrice) {
        this.kmPrice = kmPrice;
    }
}
