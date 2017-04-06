package ua.kpi.mobiledev.repository;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.domain.District;
import ua.kpi.mobiledev.domain.Street;

import static java.util.Collections.emptySet;

@Repository("streetRepository")
public class InMemStreetRepository implements StreetRepository {

    private static final String DISTRICT_STUB = "districtStub";
    private static final String CITY_STUB = "cityStub";
    private static final String ADMIN_AREA_STUB = "adminAreaStub";

//    @Resource(name = "dbMock")
//    private DBMock dbMock;

    @Override
    public Street customGet(String streetName, String cityName) {
        return new Street(null, streetName,
                new District(null, DISTRICT_STUB,
                        new City(null, CITY_STUB,
                                new AdministrationArea(null, ADMIN_AREA_STUB, emptySet()),
                                emptySet()),
                        emptySet()),
                emptySet());
//        return dbMock.getStreets().stream()
//                .filter(byStreetName(streetName))
//                .filter(byDistrict(districtName))
//                .findAny()
//                .orElseThrow(() -> new RuntimeException("No street with name " + streetName + " and district " + districtName));
    }

//    private Predicate<Street> byStreetName(String streetName) {
//        return street -> streetName.equals(street.getStreetName());
//    }
//
//    private Predicate<Street> byDistrict(String districtName) {
//        return street -> isNull(districtName) || street.getDistrict().getDistrictName().equals(districtName);
//    }

}
