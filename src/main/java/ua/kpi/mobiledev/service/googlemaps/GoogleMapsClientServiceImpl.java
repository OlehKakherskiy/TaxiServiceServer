package ua.kpi.mobiledev.service.googlemaps;

import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.exception.RequestException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.lang.String.format;
import static java.util.stream.Collectors.joining;
import static ua.kpi.mobiledev.exception.ErrorCode.INVALID_ROUTE_POINT_COUNT;

@Service("googleMapsService")
public class GoogleMapsClientServiceImpl implements GoogleMapsClientService {

    private static final String ORIGINS = "origins";
    private static final String DESTINATIONS = "destinations";
    private static final String GEOGRAPHICAL_POINT_TEMPLATE = "%s,%s";
    private static final String DELIMITER = "|";
    private static final String CALCULATE_DISTANCE_URL = format("https://maps.googleapis.com/maps/api/distancematrix/" +
            "json?units=metric&origins={%s}&destinations={%s}", ORIGINS, DESTINATIONS);

    private static final int FIRST = 0;
    private static final int MINIMAL_COUNT = 2;
    private static final int TO_KILOMETERS = 1000;

    @Resource(name = "googleMapsRequestHelper")
    private GoogleMapsRequestHelper googleMapsRequestHelper;

    public double calculateDistance(List<GeographicalPoint> geographicalPoints) {
        checkPointList(geographicalPoints);

        GoogleMapsDistanceResponse response = googleMapsRequestHelper.processGetRequest(CALCULATE_DISTANCE_URL,
                GoogleMapsDistanceResponse.class, prepareUrlParams(geographicalPoints));

        return response.getDistance() / TO_KILOMETERS;
    }

    private void checkPointList(List<GeographicalPoint> geographicalPoints) {
        if (Objects.isNull(geographicalPoints) || geographicalPoints.size() < MINIMAL_COUNT) {
            throw new RequestException(INVALID_ROUTE_POINT_COUNT);
        }
    }

    private Map<String, ?> prepareUrlParams(List<GeographicalPoint> geographicalPoints) {
        Map<String, String> urlParams = new HashMap<>();
        String origin = convertPoint(geographicalPoints.get(FIRST));
        String destinations = removeFirstPoint(geographicalPoints).stream()
                .map(this::convertPoint)
                .collect(joining(DELIMITER));
        urlParams.put(ORIGINS, origin);
        urlParams.put(DESTINATIONS, destinations);
        return urlParams;
    }

    private List<GeographicalPoint> removeFirstPoint(List<GeographicalPoint> geographicalPoints) {
        List<GeographicalPoint> points = new ArrayList<>(geographicalPoints);
        points.remove(FIRST);
        return points;
    }

    private String convertPoint(GeographicalPoint point) {
        return format(GEOGRAPHICAL_POINT_TEMPLATE, point.getLatitude(), point.getLongtitude());
    }

    public void setGoogleMapsRequestHelper(GoogleMapsRequestHelper googleMapsRequestHelper) {
        this.googleMapsRequestHelper = googleMapsRequestHelper;
    }
}
