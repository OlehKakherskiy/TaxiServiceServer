package ua.kpi.mobiledev.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.group.GroupSequenceProvider;
import ua.kpi.mobiledev.web.validation.groupprovider.RoutePointGroupProvider;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@GroupSequenceProvider(RoutePointGroupProvider.class)
public class RoutePointDto {

    public interface FullRoutePointCheck {
    }

    public interface OptionalLatitude {
    }

    public interface OptionalLongtitude {
    }

    private Long routePointId;
    private Integer routePointIndex;

    private String adminArea;

    private String locality;

    private String street;

    private String houseNumber;

    @NotNull(message = "routePoint.latitude.required", groups = FullRoutePointCheck.class)
    @Range(min = -90, max = 90, message = "routePoint.latitude.notInRange", groups = {FullRoutePointCheck.class, OptionalLatitude.class})
    private String latitude;

    @NotNull(message = "routePoint.longtitude.required")
    @Range(min = -180, max = 180, message = "routePoint.longtitude.notInRange", groups = {FullRoutePointCheck.class, OptionalLongtitude.class})
    private String longtitude;

}
