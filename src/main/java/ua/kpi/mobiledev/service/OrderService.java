package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.dto.OrderDto;

import java.util.List;

/**
 * Created by Oleg on 06.11.2016.
 */
public interface OrderService {

    Order addOrder(OrderDto orderDto);

    Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus);

    List<Order> getOrderList(Order.OrderStatus orderStatus);

    Order getOrder(Long orderId);

    Boolean deleteOrder(Long orderId);

    Order updateOrder(Long orderId, OrderDto orderDto);
}
