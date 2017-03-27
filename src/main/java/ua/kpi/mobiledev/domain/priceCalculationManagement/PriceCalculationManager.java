package ua.kpi.mobiledev.domain.priceCalculationManagement;

import ua.kpi.mobiledev.domain.Order;

public interface PriceCalculationManager {
    Order calculateOrderPrice(Order order);
}
