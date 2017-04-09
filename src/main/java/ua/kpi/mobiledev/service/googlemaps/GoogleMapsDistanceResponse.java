package ua.kpi.mobiledev.service.googlemaps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@JsonDeserialize(using = GoogleMapsDistanceDeserializer.class)
public class GoogleMapsDistanceResponse {
    private Double distance;
}
