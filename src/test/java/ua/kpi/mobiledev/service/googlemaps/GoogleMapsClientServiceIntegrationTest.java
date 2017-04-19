package ua.kpi.mobiledev.service.googlemaps;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class GoogleMapsClientServiceIntegrationTest {

    private GoogleMapsClientServiceImpl googleMapsClientService;

    @Before
    public void setUp() throws Exception {
        googleMapsClientService = new GoogleMapsClientServiceImpl();
        googleMapsClientService.setGoogleMapsRequestHelper(new GoogleMapsRequestHelper());
    }

    @Test
    public void shouldCalculateDistanceBetweenGeoPoints() {
        List<GeographicalPoint> points = asList(
                new GeographicalPoint(50.37626, 30.7007608),
                new GeographicalPoint(50.4476593, 30.4516579),
                new GeographicalPoint(49.95175, 33.62531674)
        );

        double distanceInKilometers = 241.308;
        assertThat(googleMapsClientService.calculateDistance(points), is(distanceInKilometers));
    }
}
