package ua.kpi.mobiledev.domain.additionalRequirements;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.kpi.mobiledev.domain.AdditionalRequirement;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PaymentTypeAdditionalRequirementTest {

    private static AdditionalRequirement paymentTypeAdditionalRequirement;

    @BeforeClass
    public static void beforeClass() throws Exception {
        Map<Integer, Integer> priceMap = new HashMap<>();
        priceMap.put(1, 0);
        priceMap.put(2, 5);
        paymentTypeAdditionalRequirement = new PaymentTypeAdditionalRequirement("", "", Collections.emptyMap(), priceMap);
    }

    @Test
    public void addPrice() throws Exception {
        Assert.assertEquals(0, paymentTypeAdditionalRequirement.addPrice(100.0, 1), 10e-7);
    }

    @Test
    public void addPrice_notExistedKey() throws Exception {
        Assert.assertEquals(0, paymentTypeAdditionalRequirement.addPrice(100.0, 5), 10e-7);
    }

}