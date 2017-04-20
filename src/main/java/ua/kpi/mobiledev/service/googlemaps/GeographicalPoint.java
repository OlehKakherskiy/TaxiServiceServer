package ua.kpi.mobiledev.service.googlemaps;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class GeographicalPoint {
    private double latitude;
    private double longtitude;
}
