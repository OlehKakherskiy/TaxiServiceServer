package ua.kpi.mobiledev.domain.priceCalculationManagement;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.kpi.mobiledev.domain.Order;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StrategicalPriceCalculationManagerTest {

    private static final int KM_PRICE = 10;
    private static final double PRECISION = 10e-7;

    private Order order = new Order();
    @Mock
    private CalculationStrategy calculationStrategy1;
    @Mock
    private CalculationStrategy calculationStrategy2;
    private PriceCalculationManager priceCalculationManager;

    @Before
    public void setUp() {
        List<CalculationStrategy> calculationStrategyList = asList(calculationStrategy1, calculationStrategy2);
        priceCalculationManager = new StrategicalPriceCalculationManager(calculationStrategyList, KM_PRICE);
    }

    @Test
    public void shouldCalculateOrderPrice() {
        double distance = 10.0;
        double firstStrategyResult = 50.0;
        double secondStrategyResult = 10.0;
        order.setDistance(distance);
        when(calculationStrategy1.calculatePrice(order)).thenReturn(firstStrategyResult);
        when(calculationStrategy2.calculatePrice(order)).thenReturn(secondStrategyResult);

        priceCalculationManager.calculateOrderPrice(order);

        double expectedPrice = distance * KM_PRICE + firstStrategyResult + secondStrategyResult;
        assertEquals(expectedPrice, priceCalculationManager.calculateOrderPrice(order).getPrice(), PRECISION);
    }
}