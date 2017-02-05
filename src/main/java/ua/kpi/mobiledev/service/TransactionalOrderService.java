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
import ua.kpi.mobiledev.repository.OrderRepository;

import java.text.MessageFormat;
import java.util.*;

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
        this.additionalRequirements = Collections.emptyMap();
        this.orderStatusManager = orderStatusManager;
    }

    @Override
    @Transactional
    public Order addOrder(OrderDto orderDto) {
        OrderPriceDto orderPriceDto = Objects.requireNonNull(orderDto.getOrderPrice(),
                "There's no data for order price calculation");
        User user = findUser(orderDto.getCustomerId());
        if (user.getUserType() == User.UserType.TAXI_DRIVER) {
            throw new IllegalArgumentException("Taxi driver can't add order");
        }
        Order order = new Order(null, user, null, orderDto.getStartTime(),
                orderDto.getStartPoint(), orderDto.getEndPoint(),
                calculatePrice(orderPriceDto),
                Order.OrderStatus.NEW,
                getAdditionalRequirementValueSet(orderPriceDto));
        return orderRepository.save(order);
    }

    @Override
    public Double calculatePrice(OrderPriceDto orderPriceDto) {
        if (Objects.isNull(orderPriceDto)) {
            return 0.0;
        }
        double basicPrice = Optional.ofNullable(orderPriceDto.getDistance()).orElse(0.0) * kmPrice;
        double extraPrice = getAdditionalRequirementValueSet(orderPriceDto)
                .stream()
                .map(reqEntry -> reqEntry.getAdditionalRequirement().addPrice(basicPrice, reqEntry.getRequirementValue()))
                .reduce(Double::sum).orElse(0.0);
        return basicPrice + extraPrice;
    }

    private Set<AdditionalRequirementValue> getAdditionalRequirementValueSet(OrderPriceDto orderPriceDto) {
        if (Objects.isNull(orderPriceDto)) {
            return Collections.emptySet();
        }
        Map<Integer, Integer> orderRequirements = orderPriceDto.paramsToMap();
        if (Objects.isNull(orderRequirements) || orderRequirements.isEmpty()) {
            return Collections.emptySet();
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
        return Objects.requireNonNull(additionalRequirements.get(requirementId),
                MessageFormat.format("Illegal requirement id. Id = {0}", requirementId));
    }

    private boolean isValidRequirementValueId(AdditionalRequirement additionalRequirement, Integer reqValueId) {
        Objects.requireNonNull(additionalRequirement.getRequirementValues().get(reqValueId),
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
        return Objects.isNull(orderStatus) ? mapToList(orderRepository.findAll()) : orderRepository.getAllByOrderStatus(orderStatus);
    }

    private List<Order> mapToList(Iterable<Order> all) {
        List<Order> orders = new ArrayList<>();
        all.forEach(orders::add);
        return orders;
    }

    @Override
    public Order getOrder(Long orderId) {
        Order order = orderRepository.findOne(orderId);
        if (Objects.isNull(order)) {
            throw new IllegalArgumentException(MessageFormat.format("Order with id = ''{0}'' doesn''t exist", orderId));
        }
        Hibernate.initialize(order.getTaxiDriver());
        return order;
    }

    @Override
    @Transactional
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
    @Transactional
    public Order updateOrder(Long orderId, OrderDto orderDto) {//userId is ignored
        Order order = getOrder(orderId);
        order.setStartTime(orderDto.getStartTime());
        order.setStartPoint(orderDto.getStartPoint());
        order.setEndPoint(orderDto.getEndPoint());
        OrderPriceDto orderPriceDto = orderDto.getOrderPrice();
        order.setPrice(calculatePrice(orderPriceDto));
        order.setAdditionalRequirements(getAdditionalRequirementValueSet(orderPriceDto));
        return orderRepository.save(order);
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
