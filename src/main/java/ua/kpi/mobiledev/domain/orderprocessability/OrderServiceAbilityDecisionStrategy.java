package ua.kpi.mobiledev.domain.orderprocessability;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;

public interface OrderServiceAbilityDecisionStrategy {
    boolean hasAbilityToProcessOrder(Order order, TaxiDriver driver);
}
