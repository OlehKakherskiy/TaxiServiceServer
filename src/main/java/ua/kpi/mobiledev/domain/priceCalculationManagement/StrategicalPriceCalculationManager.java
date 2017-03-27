package ua.kpi.mobiledev.domain.priceCalculationManagement;

import ua.kpi.mobiledev.domain.Order;

import java.util.List;

import static java.util.Objects.isNull;

public class StrategicalPriceCalculationManager implements PriceCalculationManager {

    private List<CalculationStrategy> calculationStrategies;
    private Integer kmPrice;

    public StrategicalPriceCalculationManager(List<CalculationStrategy> calculationStrategies, Integer kmPrice) {
        this.calculationStrategies = calculationStrategies;
        this.kmPrice = kmPrice;
    }

    @Override
    public Order calculateOrderPrice(Order order) {
        if (isNull(order)) {
            return null;
        }

        double basicPrice = calculateBasicPrice(order.getDistance());
        order.setPrice(basicPrice);

        double resultPrice = calculationStrategies.stream()
                .map(calculationStrategy -> calculationStrategy.calculatePrice(order))
                .reduce(basicPrice, Double::sum);

        order.setPrice(resultPrice);
        return order;
    }

    private double calculateBasicPrice(double distance) {
        return distance * kmPrice;
    }
}
