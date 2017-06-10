package ua.kpi.mobiledev.domain.orderprocessability;

import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.exception.RequestException;

import static ua.kpi.mobiledev.domain.Car.CarType.MINIBUS;
import static ua.kpi.mobiledev.domain.Car.CarType.PASSENGER_CAR;
import static ua.kpi.mobiledev.exception.ErrorCode.INCOMPATIBLE_CAR_TYPE_FOR_PROCESSING_ORDER;

public class HasCompatibleCarTypeDecisionStrategy implements OrderServiceAbilityDecisionStrategy {

    @Override
    public boolean hasAbilityToProcessOrder(Order order, TaxiDriver driver) {
        if (order.getCarType() == PASSENGER_CAR && driverCar(driver) == MINIBUS) {
            return true;
        }
        if (order.getCarType() != driverCar(driver)) {
            throw new RequestException(INCOMPATIBLE_CAR_TYPE_FOR_PROCESSING_ORDER);
        }
        return true;
    }

    private Car.CarType driverCar(TaxiDriver taxiDriver){
        return taxiDriver.getCar().getCarType();
    }
}
