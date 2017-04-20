package ua.kpi.mobiledev.repository.inmem;

import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.repository.OrderRepository;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

@Repository("orderRepository")
public class InMemOrderRepository implements OrderRepository {

    @Resource
    private DBMock dbMock;

    @Override
    public <S extends Order> S save(S entity) {
        if (isNull(entity.getOrderId())) {
            dbMock.addOrder(entity);
        } else {
            dbMock.replace(entity);
        }
        return entity;
    }

    @Override
    public <S extends Order> Iterable<S> save(Iterable<S> entities) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Order findOne(Long aLong) {
        return dbMock.getOrder(aLong);
    }

    @Override
    public boolean exists(Long aLong) {
        return Objects.nonNull(dbMock.getOrder(aLong));
    }

    @Override
    public Iterable<Order> findAll() {
        return dbMock.getOrders();
    }

    @Override
    public Iterable<Order> findAll(Iterable<Long> longs) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void delete(Long aLong) {
        dbMock.getOrders().stream()
                .filter(order -> order.getOrderId().equals(aLong))
                .findAny()
                .ifPresent(order -> dbMock.getOrders().remove(order));
    }

    @Override
    public void delete(Order entity) {
        dbMock.getOrders().remove(entity);
    }

    @Override
    public void delete(Iterable<? extends Order> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public List<Order> getAllByOrderStatus(@Param("orderStatus") Order.OrderStatus orderStatus) {
        return dbMock.getOrders().stream()
                .filter(order -> order.getOrderStatus().equals(orderStatus))
                .collect(toList());
    }
}