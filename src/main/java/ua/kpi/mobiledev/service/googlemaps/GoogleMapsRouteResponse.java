package ua.kpi.mobiledev.service.googlemaps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@JsonDeserialize(using = GoogleMapsRouteDeserializer.class)
public class GoogleMapsRouteResponse {
    private int distance;
    private int duration;
}