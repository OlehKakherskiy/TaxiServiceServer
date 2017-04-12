package ua.kpi.mobiledev.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class RoutePointDto {

    private String adminArea;

    private String locality;

    private String street;

    private String houseNumber;

    private String latitude;

    private String longtitude;

    private Long routePointId;

    private Integer routePointIndex;

}
