package ua.kpi.mobiledev.facade;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.data.repository.Repository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.AdministrationArea;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.domain.Country;
import ua.kpi.mobiledev.domain.District;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.repository.AddressRepository;
import ua.kpi.mobiledev.repository.AdminAreaRepository;
import ua.kpi.mobiledev.repository.CityRepository;
import ua.kpi.mobiledev.repository.CountryRepository;
import ua.kpi.mobiledev.repository.DistrictRepository;
import ua.kpi.mobiledev.repository.StreetRepository;
import ua.kpi.mobiledev.service.googlemaps.AddressBuilder;
import ua.kpi.mobiledev.service.googlemaps.AddressDto;
import ua.kpi.mobiledev.service.googlemaps.GeographicalPoint;
import ua.kpi.mobiledev.service.googlemaps.GoogleMapsClientService;
import ua.kpi.mobiledev.web.dto.RoutePointDto;

import javax.annotation.Resource;

import static java.util.Arrays.stream;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration({"classpath:appContext.xml", "classpath:repositoryMockContext.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
public class AddressFacadeImplTest {

    private static final String HOUSE_NUMBER = "7";
    private static final String STREET_NAME = "streetName";
    private static final String DISTRICT_NAME = "districtName";
    private static final String CITY_NAME = "cityName";
    private static final String ADMIN_AREA_NAME = "adminAreaName";
    private static final String COUNTRY_NAME = "countryName";

    @Resource(name = "addressRepository")
    private AddressRepository addressRepository;

    @Resource(name = "streetRepository")
    private StreetRepository streetRepository;

    @Resource(name = "districtRepository")
    private DistrictRepository districtRepository;

    @Resource(name = "cityRepository")
    private CityRepository cityRepository;

    @Resource(name = "adminAreaRepository")
    private AdminAreaRepository adminAreaRepository;

    @Resource(name = "countryRepository")
    private CountryRepository countryRepository;

    @Resource(name = "googleMapsService")
    private GoogleMapsClientService googleMapsClientService;

    @Resource(name = "addressFacade")
    private AddressFacadeImpl addressFacade;

    private static final AddressDto ADDRESS_DTO = new AddressDto();
    private static final RoutePointDto INPUT = new RoutePointDto();
    private static final double LATITUDE = 1.0;
    private static final double LONGTITUDE = 1.0;
    private static final GeographicalPoint GEOGRAPHICAL_POINT = new GeographicalPoint(LATITUDE, LONGTITUDE);
    private static final Address EXPECTED = new Address();
    private static final Street STREET = new Street();
    private static final District DISTRICT = new District();
    private static final City CITY = new City();
    private static final AdministrationArea ADMINISTRATION_AREA = new AdministrationArea();
    private static final Country COUNTRY = new Country();

    @BeforeClass
    public static void beforeClass() {
        ADDRESS_DTO.setHouseNumber(HOUSE_NUMBER);
        ADDRESS_DTO.setStreetName(STREET_NAME);
        ADDRESS_DTO.setDistrictName(DISTRICT_NAME);
        ADDRESS_DTO.setCityName(CITY_NAME);
        ADDRESS_DTO.setAdminAreaName(ADMIN_AREA_NAME);
        ADDRESS_DTO.setCountryName(COUNTRY_NAME);

        COUNTRY.setCountryName(COUNTRY_NAME);
        COUNTRY.setCountryId(1);

        ADMINISTRATION_AREA.setAdminAreaId(1);
        ADMINISTRATION_AREA.setName(ADMIN_AREA_NAME);
        ADMINISTRATION_AREA.setCountry(COUNTRY);

        CITY.setCityId(1);
        CITY.setAdministrationArea(ADMINISTRATION_AREA);
        CITY.setName(CITY_NAME);

        DISTRICT.setDistrictId(1);
        DISTRICT.setCity(CITY);
        DISTRICT.setDistrictName(DISTRICT_NAME);

        STREET.setStreetId(1);
        STREET.setDistrict(DISTRICT);
        STREET.setStreetName(STREET_NAME);

        EXPECTED.setHouseNumber(HOUSE_NUMBER);
        EXPECTED.setStreet(STREET);
        EXPECTED.setAddressId(1);

        INPUT.setLatitude(LATITUDE + "");
        INPUT.setLongtitude(LONGTITUDE + "");
    }

    @Before
    public void setUp() {
        GoogleMapsClientService spy = Mockito.spy(googleMapsClientService);
        addressFacade.setGoogleMapsClientService(spy);

        doReturn(ADDRESS_DTO).when(spy).getAddressDto(GEOGRAPHICAL_POINT);
        when(addressRepository.customGet(STREET_NAME, HOUSE_NUMBER)).thenReturn(null);
        when(streetRepository.customGet(STREET_NAME, CITY_NAME)).thenReturn(null);
        when(districtRepository.getByNameAndCityName(DISTRICT_NAME, CITY_NAME)).thenReturn(null);
        when(cityRepository.getCityByNameAndArea(CITY_NAME, ADMIN_AREA_NAME, COUNTRY_NAME)).thenReturn(null);
    }

    @Test
    public void shouldReturnAddress() {
        reset(addressRepository);
        when(addressRepository.customGet(STREET_NAME, HOUSE_NUMBER)).thenReturn(EXPECTED);

        assertThat(addressFacade.createAndGet(LATITUDE, LONGTITUDE), equalTo(EXPECTED));
    }

    @Test
    public void shouldNotSaveAddressWhenAlreadyExistsWithHouseNumAndStreetName() {
        when(addressRepository.customGet(STREET_NAME, HOUSE_NUMBER)).thenReturn(EXPECTED);

        assertThat(addressFacade.createAndGet(LATITUDE, LONGTITUDE), equalTo(EXPECTED));
        verifyZeroInteractions(streetRepository, districtRepository, cityRepository, adminAreaRepository);
    }

    @Test
    public void shouldSaveAddressWithExistedStreet() {
        reset(streetRepository);
        when(streetRepository.customGet(STREET_NAME, CITY_NAME)).thenReturn(STREET);
        Address toPersist = new AddressBuilder().start()
                .withHouseNumber(HOUSE_NUMBER)
                .withStreet(STREET)
                .build();

        doAssert(toPersist, districtRepository, cityRepository, adminAreaRepository);
    }

    @Test
    public void shouldConstructAndSaveAddressWhenDistrictExistsInDb() {
        reset(districtRepository);
        when(districtRepository.getByNameAndCityName(DISTRICT_NAME, CITY_NAME)).thenReturn(DISTRICT);

        Address toPersist = new AddressBuilder().start()
                .withHouseNumber(HOUSE_NUMBER)
                .withStreetName(STREET_NAME)
                .withDistrict(DISTRICT)
                .build();

        doAssert(toPersist, cityRepository, adminAreaRepository);
    }

    @Test
    public void shouldConstructAndSaveAddressWhenOnlySityAndAdminAreaExists() {
        reset(cityRepository);
        when(cityRepository.getCityByNameAndArea(CITY_NAME, ADMIN_AREA_NAME, COUNTRY_NAME)).thenReturn(CITY); //TODO: remove null

        Address toPersist = new AddressBuilder().start()
                .withHouseNumber(HOUSE_NUMBER)
                .withStreetName(STREET_NAME)
                .withDistrictName(DISTRICT_NAME)
                .withCity(CITY)
                .build();

        doAssert(toPersist, adminAreaRepository);
    }

    @Test
    public void shouldConstructAndSaveAddressWhenOnlyAdminAreaExists() {
        reset(adminAreaRepository);
        when(adminAreaRepository.getByName(ADMIN_AREA_NAME)).thenReturn(ADMINISTRATION_AREA);

        Address toPersist = new AddressBuilder().start()
                .withHouseNumber(HOUSE_NUMBER)
                .withStreetName(STREET_NAME)
                .withDistrictName(DISTRICT_NAME)
                .withCityName(CITY_NAME)
                .withAdminArea(ADMINISTRATION_AREA)
                .build();

        doAssert(toPersist);
    }

    @Test
    public void shouldConstructAndSaveAddressWhenOnlyCountryExistsInDb() {
        reset(countryRepository);
        when(countryRepository.findByName(COUNTRY_NAME)).thenReturn(COUNTRY);

        Address toPersist = new AddressBuilder().start()
                .withHouseNumber(HOUSE_NUMBER)
                .withStreetName(STREET_NAME)
                .withDistrictName(DISTRICT_NAME)
                .withCityName(CITY_NAME)
                .withAdminAreaName(ADMIN_AREA_NAME)
                .withCountry(COUNTRY)
                .build();

        doAssert(toPersist);
    }

    @Test
    public void shouldConstructAndPersistAddressWhenNoItPartsExist() {
        Address toPersist = new AddressBuilder().start()
                .withHouseNumber(HOUSE_NUMBER)
                .withStreetName(STREET_NAME)
                .withDistrictName(DISTRICT_NAME)
                .withCityName(CITY_NAME)
                .withAdminAreaName(ADMIN_AREA_NAME)
                .withCountryName(COUNTRY_NAME)
                .build();

        doAssert(toPersist);
    }

    private void doAssert(Address addressToPersist, Repository... checkNoInteractions) {
        when(addressRepository.save(addressToPersist)).thenReturn(EXPECTED);

        Address actual = addressFacade.createAndGet(LATITUDE, LONGTITUDE);
        assertThat(actual, equalTo(EXPECTED));

        stream(checkNoInteractions).forEach(Mockito::verifyZeroInteractions);
    }
}