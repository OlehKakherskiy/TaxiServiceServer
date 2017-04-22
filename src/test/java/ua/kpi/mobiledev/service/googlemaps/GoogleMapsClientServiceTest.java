package ua.kpi.mobiledev.service.googlemaps;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import ua.kpi.mobiledev.exception.RequestException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GoogleMapsClientServiceTest {

    private static final String CALCULATE_DISTANCE_URL = "https://maps.googleapis.com/maps/api/distancematrix/" +
            "json?units=metric&origins={origins}&destinations={destinations}";
    private static final int FIRST_DISTANCE_IN_METRES = 10000;
    private static final int SECOND_DISTANCE_IN_METRES = 5000;
    private static final int FIRST_DURATION_IN_SECONDS = 3650;
    private static final int SECOND_DURATION_IN_SECONDS = 1350;

    private GoogleMapsClientServiceImpl googleMapsClientService;
    private static final Map<String, String> FIRST_ROUTE_PART = new HashMap<>();
    private static final Map<String, String> SECOND_ROUTE_PART = new HashMap<>();
    public static final List<GeographicalPoint> TWO_POINT_ROUTE = asList(
            new GeographicalPoint(49.9627279, 33.6053331),
            new GeographicalPoint(50.4501, 30.5234)
    );
    public static final List<GeographicalPoint> THREE_POINT_ROUTE = asList(
            new GeographicalPoint(49.9627279, 33.6053331),
            new GeographicalPoint(50.4501, 30.5234),
            new GeographicalPoint(49.58826699999999, 34.551417)
    );

    @BeforeClass
    public static void beforeClass() {
        FIRST_ROUTE_PART.put("origins", "49.9627279,33.6053331");
        FIRST_ROUTE_PART.put("destinations", "50.4501,30.5234");
        SECOND_ROUTE_PART.put("origins", "50.4501,30.5234");
        SECOND_ROUTE_PART.put("destinations", "49.58826699999999,34.551417");
    }

    @Before
    public void setUp() throws Exception {
        googleMapsClientService = new GoogleMapsClientServiceImpl();
        GoogleMapsRequestHelper googleMapsRequestHelper = mock(GoogleMapsRequestHelper.class);
        googleMapsClientService.setGoogleMapsRequestHelper(googleMapsRequestHelper);
        when(googleMapsRequestHelper.processGetRequest(CALCULATE_DISTANCE_URL, GoogleMapsRouteResponse.class, FIRST_ROUTE_PART))
                .thenReturn(new GoogleMapsRouteResponse(FIRST_DISTANCE_IN_METRES, FIRST_DURATION_IN_SECONDS));
        when(googleMapsRequestHelper.processGetRequest(CALCULATE_DISTANCE_URL, GoogleMapsRouteResponse.class, SECOND_ROUTE_PART))
                .thenReturn(new GoogleMapsRouteResponse(SECOND_DISTANCE_IN_METRES, SECOND_DURATION_IN_SECONDS));
    }

    @Test
    public void shouldCalculateDistanceBetweenTwoPoints() {
        assertThat(googleMapsClientService.calculateDistance(TWO_POINT_ROUTE).getDistance(), is(FIRST_DISTANCE_IN_METRES));
    }

    @Test
    public void shouldCalculateDurationBetweenTwoPoints() {
        assertThat(googleMapsClientService.calculateDistance(TWO_POINT_ROUTE).getDuration(), is(FIRST_DURATION_IN_SECONDS));
    }

    @Test
    public void shouldSumUpDistanceBetweenSeveralPoints() {
        assertThat(googleMapsClientService.calculateDistance(THREE_POINT_ROUTE).getDistance(), is(FIRST_DISTANCE_IN_METRES + SECOND_DISTANCE_IN_METRES));
    }

    @Test
    public void shouldSumUpDurationBetweenSeveralPoints() {
        assertThat(googleMapsClientService.calculateDistance(THREE_POINT_ROUTE).getDuration(), is(FIRST_DURATION_IN_SECONDS + SECOND_DURATION_IN_SECONDS));
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