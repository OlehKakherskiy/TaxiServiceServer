package ua.kpi.mobiledev.facade;

import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.domain.District;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.service.AddressService;
import ua.kpi.mobiledev.service.AdministrationAreaService;
import ua.kpi.mobiledev.service.CityService;
import ua.kpi.mobiledev.service.DistrictService;
import ua.kpi.mobiledev.service.StreetService;
import ua.kpi.mobiledev.service.googlemaps.AddressBuilder;
import ua.kpi.mobiledev.service.googlemaps.AddressDto;
import ua.kpi.mobiledev.service.googlemaps.GeographicalPoint;
import ua.kpi.mobiledev.service.googlemaps.GoogleMapsClientService;

import javax.annotation.Resource;
import java.util.Optional;

import static java.util.Objects.isNull;

@Component("addressFacade")
@Setter
public class AddressFacadeImpl implements AddressFacade {

    @Resource(name = "addressService")
    private AddressService addressService;

    @Resource(name = "streetService")
    private StreetService streetService;

    @Resource(name = "districtService")
    private DistrictService districtService;

    @Resource(name = "cityService")
    private CityService cityService;

    @Resource(name = "adminAreaService")
    private AdministrationAreaService administrationAreaService;

    @Resource(name = "googleMapsService")
    private GoogleMapsClientService googleMapsClientService;

    @Transactional
    @Override
    public Address createAndGet(Double latitude, Double longtitude) {
        if (isNull(latitude) || isNull(longtitude)) {
            return null;
        }
        AddressDto addressPrototype = googleMapsClientService.getAddressDto(new GeographicalPoint(latitude, longtitude));

        Optional<Address> address = addressService.getAddress(addressPrototype.getStreetName(), addressPrototype.getHouseNumber());
        if (address.isPresent()) {
            return address.get();
        }

        AddressBuilder addressBuilder = new AddressBuilder();
        addressBuilder.start().withHouseNumber(addressPrototype.getHouseNumber());

        Optional<Street> street = streetService.getStreet(addressPrototype.getStreetName(), addressPrototype.getCityName());
        if (street.isPresent()) {
            return addressService.addAddress(addressBuilder.withStreet(street.get()).build());
        }
        addressBuilder.withStreetName(addressPrototype.getStreetName());

        Optional<District> district = districtService.get(addressPrototype.getDistrictName(), addressPrototype.getCityName());
        if (district.isPresent()) {
            return addressService.addAddress(addressBuilder.withDistrict(district.get()).build());
        }
        addressBuilder.withDistrictName(addressPrototype.getDistrictName());

        Optional<City> city = cityService.getCity(addressPrototype.getCityName(), addressPrototype.getAdminAreaName());
        if (city.isPresent()) {
            return addressService.addAddress(addressBuilder.withCity(city.get()).build());
        }
        addressBuilder.withCityName(addressPrototype.getCityName());

        Optional<AdministrationArea> administrationArea = administrationAreaService.getAdministrationArea(addressPrototype.getAdminAreaName());
        if (administrationArea.isPresent()) {
            return addressService.addAddress(addressBuilder.withAdminArea(administrationArea.get()).build());
        }
        return addressService.addAddress(addressBuilder.withAdminAreaName(addressPrototype.getAdminAreaName()).build());
    }
}
