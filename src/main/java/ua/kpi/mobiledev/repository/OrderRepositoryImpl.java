package ua.kpi.mobiledev.repository;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.User;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toList;

@Repository("orderRepository")
public class OrderRepositoryImpl implements OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Order findOne(Long id) {
        Order result = entityManager.find(Order.class, id);
        return (isNull(result) || result.getRemoved()) ? null : result;
    }

    @Override
    public Order save(Order entity) {
        entity.getRoutePoints().forEach(this::reattachAddress);
        if (nonNull(entity.getOrderId())) {
            entityManager.merge(entity);
        } else {
            entityManager.persist(entity);
        }
        return entity;
    }

    private RoutePoint reattachAddress(RoutePoint routePoint) {
        Address address = entityManager.find(Address.class, routePoint.getAddress().getAddressId());
        routePoint.setAddress(address);
        return routePoint;
    }

    @Override
    public void delete(Order entity) {
        entity.setRemoved(true);
        entityManager.merge(entity);
    }

    @Override
    public List<Order> getAllByOrderStatus(Order.OrderStatus orderStatus, User user) {
        if (isCustomer(user)) {
            return isNull(orderStatus) ? getForCustomerWithoutStatus(user) : getForCustomerWithStatus(orderStatus, user);
        } else {
            return isNull(orderStatus) ? getForDriverWithoutStatus(user) : getForDriverWithStatus(orderStatus, user);
        }
    }

    @Override
    public List<Order> getExpiredOrders(LocalDateTime tillTime) {
        return entityManager.createQuery("Select o from Order o where o.removed = false " +
                "and o.orderStatus = :newOrderStatus and " +
                "o.startTime <> :currentDateStart AND o.startTime < :toTime")
                .setParameter("newOrderStatus", Order.OrderStatus.NEW)
                .setParameter("currentDateStart", LocalDate.now().atStartOfDay())
                .setParameter("toTime", tillTime)
                .getResultList();
    }

    @Override
    public List<Order> getExpiredQuickRequests(long quickRequestMinutesOffset) {
        List<Order> quickRequests = entityManager.createQuery("Select o from Order o " +
                "where o.removed = false " +
                "and o.orderStatus = :newOrderStatus " +
                "and o.startTime = :currentDateStart")
                .setParameter("newOrderStatus", Order.OrderStatus.NEW)
                .setParameter("currentDateStart", LocalDate.now().atStartOfDay())
                .getResultList();
        LocalDateTime now = now();
        return quickRequests.stream()
                .filter(order -> order.getAddTime().plusMinutes(quickRequestMinutesOffset).isBefore(now))
                .collect(toList());
    }

    private boolean isCustomer(User user) {
        return user.getUserType() == User.UserType.CUSTOMER;
    }

    private List<Order> getForDriverWithoutStatus(User user) {
        return entityManager.createQuery("Select o from Order o where o.removed = false " +
                "And (o.orderStatus = :newOrderStatus " +
                "OR (o.orderStatus <> :newOrderStatus And o.taxiDriver.id = :driverId))")
                .setParameter("newOrderStatus", Order.OrderStatus.NEW)
                .setParameter("driverId", user.getId())
                .getResultList();
    }

    private List<Order> getForDriverWithStatus(Order.OrderStatus orderStatus, User user) {
        if(orderStatus == Order.OrderStatus.NEW){
            return entityManager.createQuery("Select o from Order o where o.removed = false " +
                    "And o.orderStatus = :orderStatus")
                    .setParameter("orderStatus", orderStatus)
                    .getResultList();
        }
        return entityManager.createQuery("Select o from Order o where o.removed = false " +
                "And o.taxiDriver.id = :driverId And o.orderStatus = :orderStatus")
                .setParameter("driverId", user.getId())
                .setParameter("orderStatus", orderStatus)
                .getResultList();
    }

    private List<Order> getForCustomerWithStatus(Order.OrderStatus orderStatus, User user) {
        return entityManager.createQuery("Select o from Order o where o.removed = false " +
                "And o.customer.id = :customerId And o.orderStatus = :orderStatus")
                .setParameter("customerId", user.getId())
                .setParameter("orderStatus", orderStatus)
                .getResultList();
    }

    private List<Order> getForCustomerWithoutStatus(User user) {
        return entityManager.createQuery("Select o from Order o where o.removed = false " +
                "And o.customer.id = :customerId")
                .setParameter("customerId", user.getId())
                .getResultList();
    }
}
