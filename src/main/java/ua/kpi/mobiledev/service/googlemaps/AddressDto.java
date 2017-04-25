package ua.kpi.mobiledev.service.googlemaps;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AddressDto {
    private String houseNumber;
    private String streetName;
    private String districtName;
    private String cityName;
    private String adminAreaName;
    private String countryName;
}
