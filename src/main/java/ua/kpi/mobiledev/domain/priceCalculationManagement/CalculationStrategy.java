package ua.kpi.mobiledev.domain.priceCalculationManagement;

import ua.kpi.mobiledev.domain.Order;

public interface CalculationStrategy {
    double calculatePrice(Order order);
}
