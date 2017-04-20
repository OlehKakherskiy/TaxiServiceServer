package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.Order;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("Select o from Order o where o.orderStatus=:orderStatus")
    List<ua.kpi.mobiledev.domain.Order> getAllByOrderStatus(@Param("orderStatus") ua.kpi.mobiledev.domain.Order.OrderStatus orderStatus);
}
