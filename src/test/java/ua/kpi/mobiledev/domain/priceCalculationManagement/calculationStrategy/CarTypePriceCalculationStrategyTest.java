package ua.kpi.mobiledev.domain.priceCalculationManagement.calculationStrategy;

import org.junit.Before;
import org.junit.Test;
import ua.kpi.mobiledev.domain.Car.CarType;
import ua.kpi.mobiledev.domain.Order;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ua.kpi.mobiledev.domain.Car.CarType.*;

public class CarTypePriceCalculationStrategyTest {

    private static final double BASIC_PRICE = 100.0;
    private static final double PRECISION = 1E-7;

    private static final double PASSENGER_CAR_COEFFICIENT = 0.0;
    private static final double MINIBUS_COEFFICIENT = 0.5;
    private static final double TRUCK_COEFFICIENT = 1.0;

    private static final Map<CarType, Double> multiplyCoefficients = new HashMap<>();
    private static final CarTypePriceCalculationStrategy carTypePriceCalculationStrategy =
            new CarTypePriceCalculationStrategy(multiplyCoefficients);

    static {
        multiplyCoefficients.put(PASSENGER_CAR, PASSENGER_CAR_COEFFICIENT);
        multiplyCoefficients.put(MINIBUS, MINIBUS_COEFFICIENT);
        multiplyCoefficients.put(TRUCK, TRUCK_COEFFICIENT);
    }

    private Order order;

    @Before
    public void before() throws Exception {
        order = mock(Order.class);
        when(order.getPrice()).thenReturn(BASIC_PRICE);
    }

    @Test
    public void shouldNotAddPriceWhenNoCalculationStrategyWasSpecifiedForCarType() {
        assertEquals(carTypePriceCalculationStrategy.calculatePrice(order), 0, PRECISION);
    }

    @Test
    public void shouldNotAddExtraPriceWhenCarIsPassenger() {
        when(order.getCarType()).thenReturn(PASSENGER_CAR);

        assertEquals(BASIC_PRICE * PASSENGER_CAR_COEFFICIENT,
                carTypePriceCalculationStrategy.calculatePrice(order), PRECISION);
    }

    @Test
    public void shouldAddHalfAnOrderPriceWhenCarIsMinibus() {
        when(order.getCarType()).thenReturn(MINIBUS);

        assertEquals(BASIC_PRICE * MINIBUS_COEFFICIENT,
                carTypePriceCalculationStrategy.calculatePrice(order), PRECISION);
    }

    @Test
    public void shouldAddOrderPriceAmountWhenCarIsTruck() {
        when(order.getCarType()).thenReturn(TRUCK);

        assertEquals(BASIC_PRICE * TRUCK_COEFFICIENT,
                carTypePriceCalculationStrategy.calculatePrice(order), PRECISION);
    }
}