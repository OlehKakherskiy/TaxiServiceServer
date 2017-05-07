package ua.kpi.mobiledev.repository.inmem;

import lombok.Setter;
import org.springframework.data.repository.query.Param;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.repository.OrderRepository;

import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

public class InMemOrderRepository implements OrderRepository {

    @Setter
    private DBMock dbMock;

    @Override
    public Order save(Order entity) {
        if (isNull(entity.getOrderId())) {
            dbMock.addOrder(entity);
        } else {
            dbMock.replace(entity);
        }
        return entity;
    }

    @Override
    public Order findOne(Long aLong) {
        return dbMock.getOrder(aLong);
    }

    @Override
    public void delete(Order entity) {
        dbMock.getOrders().remove(entity);
    }

    @Override
    public List<Order> getAllByOrderStatus(@Param("orderStatus") Order.OrderStatus orderStatus, User user) {
        if (user.getUserType() == User.UserType.CUSTOMER) {
            return isNull(orderStatus) ? getAllForCustomer(user) : getAllForCustomer(orderStatus, user);
        } else {
            return isNull(orderStatus) ? getAllForDriver(user) : getAllForDriver(orderStatus, user);
        }
    }

    private List<Order> getAllForCustomer(User user) {
        return dbMock.getOrders().stream()
                .filter(order -> Objects.equals(order.getCustomer().getId(), user.getId()))
                .collect(toList());
    }

    private List<Order> getAllForCustomer(Order.OrderStatus orderStatus, User user) {
        return dbMock.getOrders().stream()
                .filter(order -> Objects.equals(order.getCustomer().getId(), user.getId()))
                .filter(order -> order.getOrderStatus() == orderStatus)
                .collect(toList());
    }

    private List<Order> getAllForDriver(User user) {
        return dbMock.getOrders().stream()
                .filter(order -> order.getOrderStatus() == Order.OrderStatus.NEW || order.getTaxiDriver().getId().equals(user.getId()))
                .collect(toList());
    }

    private List<Order> getAllForDriver(Order.OrderStatus orderStatus, User user) {
        return dbMock.getOrders().stream()
                .filter(order -> order.getTaxiDriver().getId().equals(user.getId()))
                .filter(order -> order.getOrderStatus() == orderStatus)
                .collect(toList());
    }
}