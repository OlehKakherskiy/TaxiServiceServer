package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoutePoint {

    private Long routePointId;
    private Address address;
    private Integer routePointPosition;
    private Double longtitude;
    private Double latitude;

    public RoutePoint(Address address, Double longtitude, Double latitude) {
        this.address = address;
        this.longtitude = longtitude;
        this.latitude = latitude;
    }
}
