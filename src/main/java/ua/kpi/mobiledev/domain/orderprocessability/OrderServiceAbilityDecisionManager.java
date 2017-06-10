package ua.kpi.mobiledev.domain.orderprocessability;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;

public interface OrderServiceAbilityDecisionManager {
    boolean checkDriverAbilityToProcessOrder(Order order, TaxiDriver driver);
}
