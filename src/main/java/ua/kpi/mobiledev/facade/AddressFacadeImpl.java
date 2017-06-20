package ua.kpi.mobiledev.facade;

import lombok.Setter;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.kpi.mobiledev.domain.*;
import ua.kpi.mobiledev.service.*;
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

    private static final Logger LOGGER = Logger.getLogger(AddressFacadeImpl.class);

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

    @Resource(name = "countryService")
    private CountryService countryService;

    @Resource(name = "googleMapsService")
    private GoogleMapsClientService googleMapsClientService;

    @Transactional
    @Override
    public Address createAndGet(Double latitude, Double longtitude) {
        LOGGER.info(String.format("Parsing address from Google Maps for coordinates: [%s,%s] ...", latitude, longtitude));
        if (isNull(latitude) || isNull(longtitude)) {
            LOGGER.info("coordinates are absent");
            return null;
        }
        AddressDto addressPrototype = googleMapsClientService.getAddressDto(new GeographicalPoint(latitude, longtitude));
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug(String.format("Parsed address for coordinates [%s,%s]: %s", latitude, longtitude, addressPrototype));
        }

        Optional<Address> address = addressService.getAddress(addressPrototype.getStreetName(), addressPrototype.getHouseNumber());
        if (address.isPresent()) {
            return address.get();
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No prepared address in DB for DTO: " + addressPrototype);
        }

        AddressBuilder addressBuilder = new AddressBuilder();
        addressBuilder.start().withHouseNumber(addressPrototype.getHouseNumber());

        Optional<Street> street = streetService.getStreet(addressPrototype.getStreetName(), addressPrototype.getCityName());
        if (street.isPresent()) {
            return addressService.addAddress(addressBuilder.withStreet(street.get()).build());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No prepared street in DB for DTO: " + addressPrototype);
        }
        addressBuilder.withStreetName(addressPrototype.getStreetName());

        Optional<District> district = districtService.get(addressPrototype.getDistrictName(), addressPrototype.getCityName());
        if (district.isPresent()) {
            return addressService.addAddress(addressBuilder.withDistrict(district.get()).build());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No prepared district in DB for DTO: " + addressPrototype);
        }
        addressBuilder.withDistrictName(addressPrototype.getDistrictName());

        Optional<City> city = cityService.getCity(addressPrototype.getCityName(), addressPrototype.getAdminAreaName(), addressPrototype.getCountryName());
        if (city.isPresent()) {
            return addressService.addAddress(addressBuilder.withCity(city.get()).build());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No prepared city in DB for DTO: " + addressPrototype);
        }
        addressBuilder.withCityName(addressPrototype.getCityName());

        Optional<AdministrationArea> administrationArea = administrationAreaService.getAdministrationArea(addressPrototype.getAdminAreaName());
        if (administrationArea.isPresent()) {
            return addressService.addAddress(addressBuilder.withAdminArea(administrationArea.get()).build());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No prepared admin area in DB for DTO: " + addressPrototype);
        }
        addressBuilder.withAdminAreaName(addressPrototype.getAdminAreaName());

        Optional<Country> country = countryService.getCountry(addressPrototype.getCountryName());
        if (country.isPresent()) {
            return addressService.addAddress(addressBuilder.withCountry(country.get()).build());
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("No prepared country in DB for DTO: " + addressPrototype);
        }
        return addressService.addAddress(addressBuilder.withCountryName(addressPrototype.getCountryName()).build());
    }
}