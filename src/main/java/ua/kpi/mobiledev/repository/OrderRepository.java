package ua.kpi.mobiledev.repository;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository{

    Order findOne(Long id);

    Order save(Order entity);

    void delete(Order entity);

    List<Order> getAllByOrderStatus(Order.OrderStatus orderStatus, User user);

    List<Order> getExpiredOrders(LocalDateTime tillTime);

    List<Order> getExpiredQuickRequests(long quickRequestMinutesOffset);
}
