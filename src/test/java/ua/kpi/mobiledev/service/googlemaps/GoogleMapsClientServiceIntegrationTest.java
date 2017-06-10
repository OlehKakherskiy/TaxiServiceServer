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

    private static final GeographicalPoint YANHELYA_STREET = new GeographicalPoint(50.4475113,30.4527215);
    private static final GeographicalPoint SPARTAKIVSKA_STREET = new GeographicalPoint(49.9527218,33.623382);

    @Resource(name = "googleMapsService")
    private GoogleMapsClientServiceImpl googleMapsClientService;

    @Test
    public void shouldCalculateDistanceBetweenGeoPoints() {
        List<GeographicalPoint> points = asList(
                SPARTAKIVSKA_STREET,
                YANHELYA_STREET
        );

        assertThat(googleMapsClientService.calculateDistance(points).getDistance(), is(257406));
    }

    @Test
    public void shouldReturnAddressInfoByItsGeographicalPoint() {
        AddressDto expected = new AddressDto();
        expected.setHouseNumber("22");
        expected.setStreetName("Akademika Yanhelya Street");
        expected.setDistrictName("Solom'yans'kyi district");
        expected.setCityName("Kyiv");
        expected.setAdminAreaName("Kyiv city");
        expected.setCountryName("Ukraine");

        assertThat(googleMapsClientService.getAddressDto(YANHELYA_STREET), equalTo(expected));
    }

    @Test
    public void shouldSetDefaultDistrictValueIfItWasNotReturned() {
        AddressDto expected = new AddressDto();
        expected.setHouseNumber("15");
        expected.setStreetName("Spartakivs'ka Street");
        expected.setDistrictName(null);
        expected.setCityName("Myrhorod");
        expected.setAdminAreaName("Poltavs'ka oblast");
        expected.setCountryName("Ukraine");

        assertThat(googleMapsClientService.getAddressDto(SPARTAKIVSKA_STREET), equalTo(expected));
    }
}
