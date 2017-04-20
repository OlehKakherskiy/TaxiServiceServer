package ua.kpi.mobiledev.facade;

import ua.kpi.mobiledev.domain.Address;

public interface AddressFacade {
    Address createAndGet(Double latitude, Double longtitude);
}
