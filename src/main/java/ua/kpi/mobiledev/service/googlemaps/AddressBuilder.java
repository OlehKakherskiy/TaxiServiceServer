package ua.kpi.mobiledev.service.googlemaps;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.domain.District;
import ua.kpi.mobiledev.domain.Street;

import static java.util.Objects.isNull;

@Component("addressBuilder")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class AddressBuilder {

    private Address address;
    private Street street;
    private District district;
    private City city;
    private AdministrationArea administrationArea;

    public AddressBuilder start() {
        address = new Address();
        return this;
    }

    public AddressBuilder withHouseNumber(String houseNumber) {
        address.setHouseNumber(houseNumber);
        return this;
    }

    public AddressBuilder withStreet(Street street) {
        this.street = street;
        return this;
    }

    public AddressBuilder withStreetName(String streetName) {
        street = isNull(street) ? new Street() : street;
        street.setStreetName(streetName);
        return this;
    }

    public AddressBuilder withDistrict(District district) {
        this.district = district;
        return this;
    }

    public AddressBuilder withDistrictName(String districtName) {
        district = isNull(district) ? new District() : district;
        district.setDistrictName(districtName);
        return this;
    }

    public AddressBuilder withCity(City city) {
        this.city = city;
        return this;
    }

    public AddressBuilder withCityName(String cityName) {
        city = isNull(city) ? new City() : city;
        city.setName(cityName);
        return this;
    }

    public AddressBuilder withAdminArea(AdministrationArea adminArea) {
        administrationArea = adminArea;
        return this;
    }

    public AddressBuilder withAdminAreaName(String adminAreaName) {
        administrationArea = isNull(administrationArea) ? new AdministrationArea() : administrationArea;
        administrationArea.setName(adminAreaName);
        return this;
    }

    public Address build() {
        if(isNull(address)) {
            return null;
        }
        if(isNull(street)){
            return address;
        }
        address.setStreet(street);
        if(isNull(district)){
            return address;
        }
        street.setDistrict(district);
        if(isNull(city)){
            return address;
        }
        district.setCity(city);
        if(isNull(administrationArea)){
            return address;
        }
        city.setAdministrationArea(administrationArea);
        return address;
    }
}
