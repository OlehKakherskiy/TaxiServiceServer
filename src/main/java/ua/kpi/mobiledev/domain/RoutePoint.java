package ua.kpi.mobiledev.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.GenerationType.IDENTITY;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "route_point")
public class RoutePoint {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "route_point_id")
    private Long routePointId;

    @OneToOne(cascade = {PERSIST, MERGE})
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "point_index")
    private Integer routePointPosition;

    @Column(name = "longtitude")
    private Double longtitude;

    @Column(name = "latitude")
    private Double latitude;


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
