package ua.kpi.mobiledev.domain.additionalRequirements;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import ua.kpi.mobiledev.domain.AdditionalRequirement;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.exception.ResourceNotFoundException;
import ua.kpi.mobiledev.testCategories.UnitTest;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Category(UnitTest.class)
public class CarTypeAdditionalRequirementTest {

    private static AdditionalRequirement additionalRequirement;

    @BeforeClass
    public static void beforeClass() {
        additionalRequirement = new CarTypeAdditionalRequirement("", "",
                getReqValues(Car.CarType.PASSENGER_CAR, Car.CarType.MINIBUS, Car.CarType.TRUCK),
                multCoefficients(new Car.CarType[]{Car.CarType.PASSENGER_CAR, Car.CarType.MINIBUS, Car.CarType.TRUCK}, 0.0, 1.0, 2.0));
    }

    private static Map<Integer, String> getReqValues(Car.CarType... types) {
        Map<Integer, String> reqValues = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            reqValues.put(i, types[i].name());
        }
        reqValues.put(3, "VALUE_THAT_CAN'T_BE_CAST_TO_CARTYPE"); //for checking exception while casting to CarType
        return reqValues;
    }

    private static Map<Car.CarType, Double> multCoefficients(Car.CarType[] types, Double... coefficients) {
        Map<Car.CarType, Double> res = new HashMap<>();
        for (int i = 0; i < types.length; i++) {
            res.put(types[i], coefficients[i]);
        }
        return res;
    }

    @Test
    public void addPrice_allIsOk() throws Exception {
        Assert.assertEquals(100.0, additionalRequirement.addPrice(100.0, 1), 1e-7);
    }

    @Test(expected = NullPointerException.class)
    public void addPrice_noTypeWithTargetId() throws Exception {
        additionalRequirement.addPrice(200.0, 5);
    }

    @Test(expected = ResourceNotFoundException.class)
    public void addPrice_targetIdIsNull() throws Exception {
        additionalRequirement.addPrice(200.0, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void addPrice_noTypeWithTargetTypeName() throws Exception {
        additionalRequirement.addPrice(200.0, 3);
    }

    @Test
    public void addPrice_multiplierIsNull() throws Exception {
        Map<Integer, String> valuesIds = new HashMap<>();
        valuesIds.put(1, "truck");
        AdditionalRequirement emptyRequirement = new CarTypeAdditionalRequirement("", "", valuesIds, Collections.emptyMap());
        Assert.assertEquals(0.0, emptyRequirement.addPrice(100.0, 1), 1e-7);
    }
}