package ua.kpi.mobiledev.domain.priceCalculationManagement.calculationStrategy;

import lombok.Setter;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.priceCalculationManagement.CalculationStrategy;

public class StartPriceCalculationStrategy implements CalculationStrategy{

    @Setter
    private Integer startPrice;

    @Override
    public double calculatePrice(Order order) {
        return startPrice;
    }
}
