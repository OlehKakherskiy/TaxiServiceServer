package ua.kpi.mobiledev.service.googlemaps;

import org.junit.Before;
import org.junit.Test;
import ua.kpi.mobiledev.exception.RequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoogleMapsClientServiceTest {

    private static final String CALCULATE_DISTANCE_URL = "https://maps.googleapis.com/maps/api/distancematrix/" +
            "json?units=metric&origins={origins}&destinations={destinations}";
    private static final double DISTANCE_IN_METRES = 10000.0;
    private static final double DISTANCE_IN_KILOMETERS = DISTANCE_IN_METRES / 1000.0;

    private GoogleMapsRequestHelper googleMapsRequestHelper;
    private GoogleMapsClientServiceImpl googleMapsClientService;

    @Before
    public void setUp() throws Exception {
        googleMapsClientService = new GoogleMapsClientServiceImpl();
        googleMapsRequestHelper = mock(GoogleMapsRequestHelper.class);
        googleMapsClientService.setGoogleMapsRequestHelper(googleMapsRequestHelper);
    }

    @Test
    public void shouldCalculateDistanceBetweenGeoPoints() {
        Map<String, String> urlVariables = new HashMap<>();
        urlVariables.put("origins", "50.37626,30.7007608");
        urlVariables.put("destinations", "49.95175,30.7007608|49.95175,33.6253167");

        when(googleMapsRequestHelper.processGetRequest(CALCULATE_DISTANCE_URL, GoogleMapsDistanceResponse.class, urlVariables))
                .thenReturn(new GoogleMapsDistanceResponse(DISTANCE_IN_METRES));
        List<GeographicalPoint> points = asList(
                new GeographicalPoint(50.37626, 30.7007608),
                new GeographicalPoint(49.95175, 30.7007608),
                new GeographicalPoint(49.95175, 33.6253167)
        );

        assertThat(googleMapsClientService.calculateDistance(points), is(DISTANCE_IN_KILOMETERS));
        verify(googleMapsRequestHelper).processGetRequest(CALCULATE_DISTANCE_URL, GoogleMapsDistanceResponse.class, urlVariables);
    }

    @Test(expected = RequestException.class)
    public void shouldThrowExceptionWhenListIsNull() {
        googleMapsClientService.calculateDistance(null);
    }

    @Test(expected = RequestException.class)
    public void shouldThrowExceptionWhenListHasLessElementsThanExpected() {
        googleMapsClientService.calculateDistance(asList(new GeographicalPoint(0.0, 0.0)));
    }
}