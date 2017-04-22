package ua.kpi.mobiledev.service.googlemaps;

import java.util.List;

public interface GoogleMapsClientService {
    GoogleMapsRouteResponse calculateDistance(List<GeographicalPoint> geographicalPoints);
    AddressDto getAddressDto(GeographicalPoint geographicalPoint);
}
