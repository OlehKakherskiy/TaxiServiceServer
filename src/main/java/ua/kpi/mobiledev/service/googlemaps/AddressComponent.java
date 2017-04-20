package ua.kpi.mobiledev.service.googlemaps;

import lombok.Data;

import java.util.List;

@Data
public class AddressComponent {
    private String name;
    private List<String> types;
}
