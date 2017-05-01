package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Order;

import java.util.List;

public interface OrderService {

    Order addOrder(Order order, Integer userId);

    Order getOrderRouteParams(Order notCompletedOrder);

    Order changeOrderStatus(Long orderId, Integer userId, Order.OrderStatus orderStatus);

    List<Order> getOrderList(Order.OrderStatus orderStatus, Integer userId);

    Order getOrder(Long orderId);

    void deleteOrder(Long orderId, Integer userId);

    Order updateOrder(Order orderPrototype, Integer userId);
}
