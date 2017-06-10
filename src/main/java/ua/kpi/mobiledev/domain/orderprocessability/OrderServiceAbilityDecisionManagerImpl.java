package ua.kpi.mobiledev.domain.orderprocessability;

import lombok.Setter;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.exception.RequestException;

import java.util.List;

import static ua.kpi.mobiledev.exception.ErrorCode.DRIVER_HAS_NO_ABILITY_TO_PROCESS_ORDER_GENERAL_MESSAGE;

public class OrderServiceAbilityDecisionManagerImpl implements OrderServiceAbilityDecisionManager {

    @Setter
    private List<OrderServiceAbilityDecisionStrategy> checkAbilityStrategies;

    @Override
    public boolean checkDriverAbilityToProcessOrder(Order order, TaxiDriver driver) {

        for (OrderServiceAbilityDecisionStrategy abilityStrategy : checkAbilityStrategies) {
            if (!abilityStrategy.hasAbilityToProcessOrder(order, driver)) {
                throw new RequestException(DRIVER_HAS_NO_ABILITY_TO_PROCESS_ORDER_GENERAL_MESSAGE);
            }
        }
        return true;
    }
}
