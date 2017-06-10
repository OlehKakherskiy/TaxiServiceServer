package ua.kpi.mobiledev.domain.orderprocessability;

import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.exception.ErrorCode;
import ua.kpi.mobiledev.exception.RequestException;

public class HasEnoughPlaceDecisionStrategy implements OrderServiceAbilityDecisionStrategy {

    @Override
    public boolean hasAbilityToProcessOrder(Order order, TaxiDriver driver) {
        if (order.getPassengerCount() > driver.getCar().getSeatNumber()) {
            throw new RequestException(ErrorCode.HAS_NOT_ENOUGH_SEAT_NUMBER);
        }
        return true;
    }
}
