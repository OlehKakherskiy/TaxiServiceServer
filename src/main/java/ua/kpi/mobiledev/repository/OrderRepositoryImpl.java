package ua.kpi.mobiledev.repository;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository("orderRepository")
public class OrderRepositoryImpl implements CustomOrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public <S extends Order> S save(S entity) {
        entity.getRoutePoints().forEach(this::reattachAddress);
        entityManager.persist(entity);
        return entity;
    }

    private RoutePoint reattachAddress(RoutePoint routePoint) {
        Address address = entityManager.find(Address.class, routePoint.getAddress().getAddressId());
        routePoint.setAddress(address);
        return routePoint;
    }
}
