package ua.kpi.mobiledev.web.converter;

import lombok.Setter;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.domain.dto.RoutePointDto;
import ua.kpi.mobiledev.facade.AddressFacade;

import javax.annotation.Resource;

import static java.lang.Double.parseDouble;

@Component("routePointConverter")
@Setter
public class RoutePointConverter implements CustomConverter<RoutePointDto, RoutePoint> {

    @Resource(name = "addressFacade")
    private AddressFacade addressFacade;

    @Override
    public void convert(RoutePointDto routePointDto, RoutePoint routePoint) {
        routePoint.setAddress(addressFacade.createAndGet(routePointDto));
        routePoint.setLatitude(parseDouble(routePointDto.getLatitude()));
        routePoint.setLongtitude(parseDouble(routePointDto.getLongtitude()));
    }

    @Override
    public void reverseConvert(RoutePoint routePoint, RoutePointDto routePointDto) {
        Address address = routePoint.getAddress();
        Street street = routePoint.getAddress().getStreet();
        City city = street.getDistrict().getCity();
        AdministrationArea administrationArea = city.getAdministrationArea();
        routePointDto.setAdminArea(administrationArea.getName());
        routePointDto.setLocality(city.getName());
        routePointDto.setStreet(street.getStreetName());
        routePointDto.setHouseNumber(address.getHouseNumber());
        routePointDto.setLatitude(routePoint.getLatitude().toString());
        routePointDto.setLongtitude(routePoint.getLongtitude().toString());
    }
}