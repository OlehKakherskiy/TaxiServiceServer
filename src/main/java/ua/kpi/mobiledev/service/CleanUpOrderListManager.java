package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Order;

public interface CleanUpOrderListManager {

    boolean isExpired(Order order);

    void expire(Order order);

    void closeExpiredOrders();

    void closeExpiredQuickRequests();
}
