package ua.kpi.mobiledev.service.googlemaps;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.util.List;

@Data
@JsonDeserialize(using = AddressInfoDeserializer.class)
public class AddressInfo {
    private List<AddressComponent> components;
}
