package ua.kpi.mobiledev.facade;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.service.AddressService;
import ua.kpi.mobiledev.service.StreetService;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import javax.annotation.Resource;

import static java.util.Objects.isNull;

@Component("addressFacade")
public class AddressFacadeImpl implements AddressFacade {

    @Resource(name = "addressService")
    private AddressService addressService;

    @Resource(name = "streetService")
    private StreetService streetService;

    @Override
    public Address createAndGet(RoutePointDto routePointDto) {
        Address address = addressService.getAddress(routePointDto.getStreet(), routePointDto.getHouseNumber());
        return isNull(address) ? addAddress(routePointDto) : address;
    }

    private Address addAddress(RoutePointDto routePointDto) {
        Street street = streetService.getStreet(routePointDto.getStreet(), routePointDto.getAdminArea());
        return addressService.addAddress(street, routePointDto.getHouseNumber());
    }
}
