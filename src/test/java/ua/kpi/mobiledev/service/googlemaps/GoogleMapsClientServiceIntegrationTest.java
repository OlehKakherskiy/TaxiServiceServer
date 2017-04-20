package ua.kpi.mobiledev.service.googlemaps;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@ContextConfiguration("classpath:googleMapsTestContext.xml")
@DirtiesContext
public class GoogleMapsClientServiceIntegrationTest {

    private static final GeographicalPoint YANHELYA_STREET = new GeographicalPoint(50.4475705, 30.4527254);
    private static final GeographicalPoint SPARTAKIVSKA_STREET = new GeographicalPoint(49.95175, 33.62531674);

    @Resource(name = "googleMapsService")
    private GoogleMapsClientServiceImpl googleMapsClientService;

    @Test
    public void shouldCalculateDistanceBetweenGeoPoints() {
        List<GeographicalPoint> points = asList(
                new GeographicalPoint(50.37626, 30.7007608),
                YANHELYA_STREET,
                SPARTAKIVSKA_STREET
        );

        double distanceInKilometers = 241.308;
        assertThat(googleMapsClientService.calculateDistance(points), is(distanceInKilometers));
    }

    @Test
    public void shouldReturnAddressInfoByItsGeographicalPoint() {
        AddressDto expected = new AddressDto();
        expected.setHouseNumber("22");
        expected.setStreetName("Akademika Yanhelya Street");
        expected.setDistrictName("Solom'yans'kyi district");
        expected.setCityName("Kyiv");
        expected.setAdminAreaName("Kyiv city");

        assertThat(googleMapsClientService.getAddressDto(YANHELYA_STREET), equalTo(expected));
    }

    @Test
    public void shouldSetDefaultDistrictValueIfItWasNotReturned() {
        AddressDto expected = new AddressDto();
        expected.setHouseNumber("17");
        expected.setStreetName("Spartakivs'ka Street");
        expected.setDistrictName("NO_DISTRICT_INFO");
        expected.setCityName("Myrhorod");
        expected.setAdminAreaName("Poltavs'ka oblast");

        assertThat(googleMapsClientService.getAddressDto(SPARTAKIVSKA_STREET), equalTo(expected));
    }
}
