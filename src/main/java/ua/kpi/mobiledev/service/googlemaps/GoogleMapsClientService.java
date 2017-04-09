package ua.kpi.mobiledev.service.googlemaps;

import java.util.List;

public interface GoogleMapsClientService {
    double calculateDistance(List<GeographicalPoint> geographicalPoints);
}
