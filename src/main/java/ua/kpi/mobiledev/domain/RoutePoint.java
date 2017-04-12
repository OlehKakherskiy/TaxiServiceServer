package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoutePoint that = (RoutePoint) o;

        if (routePointId != null ? !routePointId.equals(that.routePointId) : that.routePointId != null) return false;
        if (!address.equals(that.address)) return false;
        if (!routePointPosition.equals(that.routePointPosition)) return false;
        if (!longtitude.equals(that.longtitude)) return false;
        return latitude.equals(that.latitude);
    }

    @Override
    public int hashCode() {
        int result = routePointId != null ? routePointId.hashCode() : 0;
        result = 31 * result + address.hashCode();
        result = 31 * result + routePointPosition.hashCode();
        result = 31 * result + longtitude.hashCode();
        result = 31 * result + latitude.hashCode();
        return result;
    }
}
