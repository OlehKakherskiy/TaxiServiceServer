package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.dto.OrderDto;

import java.util.List;

public interface OrderService {

    Order addOrder(OrderDto orderDto);

    Double calculatePrice(Order notCompletedOrder);

    Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus);

    List<Order> getOrderList(Order.OrderStatus orderStatus);

    Order getOrder(Long orderId);

    void deleteOrder(Long orderId, Integer userId);

    Order updateOrder(Long orderId, Integer userId, OrderDto orderDto);
}
