package ua.kpi.mobiledev.facade;

import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.dto.RoutePointDto;

public interface AddressFacade {
    Address createAndGet(RoutePointDto routePointDto);
}
