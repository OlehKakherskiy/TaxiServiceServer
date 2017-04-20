package ua.kpi.mobiledev.service.googlemaps;

import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.web.converter.CustomConverter;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Component("addressInfoToDtoConverter")
public class AddressInfoToDtoConverter implements CustomConverter<AddressInfo, AddressDto> {

    private static final String NO_DISTRICT_INFO = "NO_DISTRICT_INFO";
    private List<String> infoTypes = Arrays.asList("street_number", "route", "locality", "sublocality",
            "administrative_area_level_1", "administrative_area_level_2");

    @Override
    public void convert(AddressInfo source, AddressDto target) {
        setNamesFor(target, filterComponentsByInfoType(source));
    }

    private List<AddressComponent> filterComponentsByInfoType(AddressInfo source) {
        return source.getComponents()
                .stream()
                .filter(this::hasExpectedInfoType)
                .collect(toList());
    }

    private boolean hasExpectedInfoType(AddressComponent addressComponent) {
        List<String> addressComponentTypes = addressComponent.getTypes();
        addressComponentTypes.retainAll(infoTypes);
        return !addressComponentTypes.isEmpty();
    }

    private void setNamesFor(AddressDto addressDto, List<AddressComponent> components) {
        for (AddressComponent addressComponent : components) {
            String addressComponentValue = addressComponent.getName();
            switch (addressComponent.getTypes().get(0)) {
                case "street_number": {
                    addressDto.setHouseNumber(addressComponentValue);
                    break;
                }
                case "route": {
                    addressDto.setStreetName(addressComponentValue);
                    break;
                }
                case "sublocality": {
                    addressDto.setDistrictName(addressComponentValue);
                    break;
                }
                case "locality": {
                    addressDto.setCityName(addressComponentValue);
                    break;
                }
                case "administrative_area_level_1":
                case "administrative_area_level_2": {
                    addressDto.setAdminAreaName(addressComponentValue);
                    break;
                }
            }
        }
        if (Objects.isNull(addressDto.getDistrictName())) {
            addressDto.setDistrictName(NO_DISTRICT_INFO);
        }
    }


    @Override
    public void reverseConvert(AddressDto source, AddressInfo target) {
    }
}
