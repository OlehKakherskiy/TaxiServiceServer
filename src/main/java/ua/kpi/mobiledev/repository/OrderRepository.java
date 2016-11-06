package ua.kpi.mobiledev.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Order;

import java.util.List;

/**
 * Created by Oleg on 06.11.2016.
 */
@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    List<Order> getAllByOrderStatus(Order.OrderStatus orderStatus);
}
