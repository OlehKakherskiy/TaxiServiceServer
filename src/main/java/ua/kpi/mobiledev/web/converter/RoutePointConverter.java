package ua.kpi.mobiledev.web.converter;

import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.facade.AddressFacade;
import ua.kpi.mobiledev.service.AddressService;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import javax.annotation.Resource;
import java.util.Optional;

import static java.lang.Double.parseDouble;

@Component("routePointConverter")
@Setter
public class RoutePointConverter implements CustomConverter<RoutePointDto, RoutePoint> {

    private static final Logger LOGGER = Logger.getLogger(RoutePointConverter.class);

    @Resource(name = "addressFacade")
    private AddressFacade addressFacade;

    @Resource(name = "addressService")
    private AddressService addressService;

    @Override
    public void convert(RoutePointDto routePointDto, RoutePoint routePoint) {
        routePoint.setRoutePointId(routePointDto.getRoutePointId());
        routePoint.setRoutePointPosition(routePointDto.getRoutePointIndex());
        routePoint.setLatitude(routePointDto.getLatitude() == null ? null : parseDouble(routePointDto.getLatitude()));
        routePoint.setLongtitude(routePointDto.getLongtitude() == null ? null : parseDouble(routePointDto.getLongtitude()));
        Optional<Address> addressOptional = tryGetExistedAddress(routePoint);
        if (addressOptional.isPresent()) {
            LOGGER.info(String.format("address for coordinates [%s,%s] was found in the local storage", routePoint.getLatitude(), routePoint.getLongtitude()));
        }
        routePoint.setAddress(addressOptional.isPresent() ? addressOptional.get() : createAndGetAddress(routePoint));
    }

    private Optional<Address> tryGetExistedAddress(RoutePoint routePoint) {
        return addressService.getAddress(routePoint.getLatitude(), routePoint.getLongtitude());
    }

    private Address createAndGetAddress(RoutePoint routePoint) {
        return addressFacade.createAndGet(routePoint.getLatitude(), routePoint.getLongtitude());
    }

    @Override
    public void reverseConvert(RoutePoint routePoint, RoutePointDto routePointDto) {
        Address address = routePoint.getAddress();
        Street street = routePoint.getAddress().getStreet();
        City city = street.getDistrict().getCity();
        AdministrationArea administrationArea = city.getAdministrationArea();
        routePointDto.setRoutePointId(routePoint.getRoutePointId());
        routePointDto.setAdminArea(administrationArea.getName());
        routePointDto.setLocality(city.getName());
        routePointDto.setStreet(street.getStreetName());
        routePointDto.setHouseNumber(address.getHouseNumber());
        routePointDto.setLatitude(routePoint.getLatitude().toString());
        routePointDto.setLongtitude(routePoint.getLongtitude().toString());
    }
}
