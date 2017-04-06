package ua.kpi.mobiledev.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RoutePointDto {

    private String adminArea;

    private String locality;

    private String street;

    private String houseNumber;

    private String latitude;

    private String longtitude;

}
