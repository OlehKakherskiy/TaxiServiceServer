package ua.kpi.mobiledev.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.repository.OrderRepository;

import javax.annotation.Resource;
import java.util.List;

import static java.lang.String.format;
import static java.time.LocalDateTime.now;

@Component("cleanUpOrderListManager")
public class CleanUpOrderListManagerImpl implements CleanUpOrderListManager {

    private static final Logger LOGGER = Logger.getLogger(CleanUpOrderListManagerImpl.class);

    @Value("${order.cleanup.quickRequestExpirationMinutes}")
    private Integer quickRequestExpirationSlotMinutes = 60;

    @Resource(name = "orderRepository")
    private OrderRepository orderRepository;

    @Override
    public boolean isExpired(Order order) {
        if (isQuickRequest(order)) {
            return order.getAddTime().plusMinutes(quickRequestExpirationSlotMinutes).isBefore(now());
        } else {
            return order.getStartTime().isBefore(now());
        }
    }

    @Override
    @Transactional
    public void expire(Order order) {
        order.setOrderStatus(Order.OrderStatus.EXPIRED);
        orderRepository.save(order);
    }

    private boolean isQuickRequest(Order order) {
        return order.getStartTime().isEqual(order.getStartTime().toLocalDate().atStartOfDay());
    }

    @Override
    @Scheduled(cron = "${order.cleanup.cleanupOrdersCron}")
    @Transactional
    public void closeExpiredOrders() {
        List<Order> orderList = orderRepository.getExpiredOrders(now());
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("Clean up manager is expiring '%s' planned orders since last run", orderList.size()));
        }
        orderList.forEach(this::expire);
    }

    @Override
    @Transactional
    @Scheduled(cron = "${order.cleanup.cleanupQuickRequestsCron}")
    public void closeExpiredQuickRequests() {
        List<Order> orderList = orderRepository.getExpiredQuickRequests(quickRequestExpirationSlotMinutes);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(format("Clean up manager is expiring '%s' quick requests since last run", orderList.size()));
        }
        orderList.forEach(this::expire);
    }
}
