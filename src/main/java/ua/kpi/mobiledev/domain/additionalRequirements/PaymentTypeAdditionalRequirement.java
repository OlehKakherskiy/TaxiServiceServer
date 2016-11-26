package ua.kpi.mobiledev.domain.additionalRequirements;

import ua.kpi.mobiledev.domain.AdditionalRequirement;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class PaymentTypeAdditionalRequirement extends AdditionalRequirement {

    private Map<Integer, Integer> paymentTypeExtraPrice;

    public PaymentTypeAdditionalRequirement() {
        paymentTypeExtraPrice = Collections.emptyMap();
    }

    public PaymentTypeAdditionalRequirement(String requirementName, String priceDescription,
                                            Map<Integer, String> requirementValues,
                                            Map<Integer, Integer> paymentTypeExtraPrice) {
        super(requirementName, priceDescription, requirementValues);
        this.paymentTypeExtraPrice = Objects.isNull(paymentTypeExtraPrice) ? Collections.emptyMap() : paymentTypeExtraPrice;
    }

    @Override
    public double addPrice(Double basicPrice, Integer requirementValueId) {
        return Objects.isNull(requirementValueId) ? 0 : paymentTypeExtraPrice.getOrDefault(requirementValueId, 0);
    }
}
