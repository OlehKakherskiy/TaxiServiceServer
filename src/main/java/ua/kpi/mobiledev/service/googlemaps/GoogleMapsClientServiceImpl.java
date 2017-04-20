package ua.kpi.mobiledev.service.googlemaps;

import lombok.Setter;
import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.web.converter.CustomConverter;

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
@Setter
public class GoogleMapsClientServiceImpl implements GoogleMapsClientService {

    private static final String ORIGINS = "origins";
    private static final String DESTINATIONS = "destinations";
    private static final String GEOGRAPHICAL_POINT_TEMPLATE = "%s,%s";
    private static final String DELIMITER = "|";
    private static final String CALCULATE_DISTANCE_URL = format("https://maps.googleapis.com/maps/api/distancematrix/" +
            "json?units=metric&origins={%s}&destinations={%s}", ORIGINS, DESTINATIONS);

    private static final String LONGTITUDE = "lng";
    private static final String LATITUDE = "lat";
    private static final String GET_ADDRESS_INFO_FOR_COORDINATES =
            format("https://maps.googleapis.com/maps/api/geocode/json?latlng={%s},{%s}&language=en", LATITUDE, LONGTITUDE);

    private static final int FIRST = 0;
    private static final int MINIMAL_COUNT = 2;
    private static final int TO_KILOMETERS = 1000;

    @Resource(name = "googleMapsRequestHelper")
    private GoogleMapsRequestHelper googleMapsRequestHelper;

    @Resource(name = "addressInfoToDtoConverter")
    private CustomConverter<AddressInfo, AddressDto> addressInfoToDtoConverter;

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

    @Override
    public AddressDto getAddressDto(GeographicalPoint geographicalPoint) {
        Map<String, String> urlParams = new HashMap<>();
        urlParams.put(LATITUDE, "" + geographicalPoint.getLatitude());
        urlParams.put(LONGTITUDE, "" + geographicalPoint.getLongtitude());
        AddressInfo addressInfo = googleMapsRequestHelper.processGetRequest(GET_ADDRESS_INFO_FOR_COORDINATES,
                AddressInfo.class, urlParams);
        AddressDto result = new AddressDto();
        addressInfoToDtoConverter.convert(addressInfo, result);
        return result;
    }
}
