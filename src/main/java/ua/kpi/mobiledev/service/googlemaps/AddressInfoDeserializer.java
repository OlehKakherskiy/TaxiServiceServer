package ua.kpi.mobiledev.service.googlemaps;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddressInfoDeserializer extends JsonDeserializer<AddressInfo> {

    private static final int FIRST_ELEMENT = 0;

    @Override
    public AddressInfo deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
        ArrayNode results = (ArrayNode) jsonParser.getCodec().readTree(jsonParser).get("results");
        ArrayNode addressComponents = (ArrayNode) results.get(FIRST_ELEMENT).get("address_components");
        List<AddressComponent> addressComponentList = new ArrayList<>();
        addressComponents.forEach(jsonNode -> addressComponentList.add(mapToAddressComponent(jsonNode)));
        AddressInfo addressInfo = new AddressInfo();
        addressInfo.setComponents(addressComponentList);
        return addressInfo;
    }

    private AddressComponent mapToAddressComponent(JsonNode jsonNode) {
        AddressComponent addressComponent = new AddressComponent();
        addressComponent.setName(jsonNode.get("long_name").textValue());
        List<String> types = new ArrayList<>();
        jsonNode.get("types").forEach(jsonNode1 -> types.add(jsonNode1.textValue()));
        addressComponent.setTypes(types);
        return addressComponent;
    }
}
