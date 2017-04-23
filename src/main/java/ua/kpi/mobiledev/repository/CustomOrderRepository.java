package ua.kpi.mobiledev.repository;

import ua.kpi.mobiledev.domain.Order;

public interface CustomOrderRepository {

    <S extends Order> S save(S order);
}
