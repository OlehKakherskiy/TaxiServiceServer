package ua.kpi.mobiledev.domain.priceCalculationManagement.calculationStrategy;

import ua.kpi.mobiledev.domain.Car.CarType;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.priceCalculationManagement.CalculationStrategy;

import java.util.Map;

import static java.util.Optional.ofNullable;

public class CarTypePriceCalculationStrategy implements CalculationStrategy {

    private Map<CarType, Double> multiplyCoefficients;

    public CarTypePriceCalculationStrategy(Map<CarType, Double> multiplyCoefficients) {
        this.multiplyCoefficients = multiplyCoefficients;
    }

    @Override
    public double calculatePrice(Order order) {
        return order.getPrice() * ofNullable(multiplyCoefficients.get(order.getCarType())).orElse(0.0);
    }
}
