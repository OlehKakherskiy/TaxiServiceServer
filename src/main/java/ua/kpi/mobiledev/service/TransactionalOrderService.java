package ua.kpi.mobiledev.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.AdditionalRequirement;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.orderStatusManagement.OrderStatusManager;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.domain.dto.OrderDto;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Oleg on 06.11.2016.
 */
@Service
public class TransactionalOrderService implements OrderService {

    private OrderRepository orderRepository;

    private UserService userService;

    private Set<AdditionalRequirement> additionalRequirements;

    private OrderStatusManager orderStatusManager;

    @Autowired
    public TransactionalOrderService(OrderRepository orderRepository, UserService userService, Set<AdditionalRequirement> additionalRequirements, OrderStatusManager orderStatusManager) {
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
        Order order = new Order(null, user, null, orderDto.getStartTime(),
                orderDto.getStartPoint(), orderDto.getEndPoint(),
                calculateOrderPrice(orderDto),
                Order.OrderStatus.NEW,
                getAdditionalRequirementMap(orderDto));
        return orderRepository.save(order);
    }

    private double calculateOrderPrice(OrderDto orderDto) {
        return 0.0; //TODO: change implementation
    }

    private Map<AdditionalRequirement, Integer> getAdditionalRequirementMap(OrderDto orderDto) {
        if (orderDto.getAdditionalRequestValueMap() != null && orderDto.getAdditionalRequestValueMap().size() != 0) {
            return Collections.emptyMap();
        } else {
            return Collections.emptyMap(); //TODO: change implementation
        }
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
    public Boolean deleteOrder(Long orderId) {
        return null;
    }

    @Override
    public Order updateOrder(Long orderId, OrderDto orderDto) {
        Order order = findOrder(orderId);
        order.setStartTime((orderDto.getStartTime() != null) ? orderDto.getStartTime() : order.getStartTime());
        order.setStartPoint((orderDto.getStartPoint() != null) ? orderDto.getStartPoint() : order.getStartPoint());
        order.setEndPoint((orderDto.getEndPoint() != null) ? orderDto.getEndPoint() : order.getEndPoint());
        Map<AdditionalRequirement, Integer> addReqs = getAdditionalRequirementMap(orderDto);
        order.setAdditionalRequirementList((!addReqs.isEmpty()) ? addReqs : order.getAdditionalRequirementList());
        orderRepository.save(order);
        return order;
    }

    private User findUser(Integer userId) {
        return userService.getUser(userId);
    }

    private Order findOrder(Long orderId) {
        return orderRepository.findOne(orderId);
    }
}
