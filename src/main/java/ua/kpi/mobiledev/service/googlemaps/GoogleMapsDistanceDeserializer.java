package ua.kpi.mobiledev.service.googlemaps;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;

public class GoogleMapsDistanceDeserializer extends JsonDeserializer<GoogleMapsDistanceResponse> {

    private static final String ROWS = "rows";
    private static final String ELEMENTS = "elements";
    private static final String DISTANCE = "distance";
    private static final int FIRST_NODE = 0;
    private static final String VALUE_FIELD = "value";

    @Override
    public GoogleMapsDistanceResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode rowsElement = jsonParser.getCodec().readTree(jsonParser).get(ROWS);
        ArrayNode elementsNode = (ArrayNode) rowsElement.get(FIRST_NODE).get(ELEMENTS);
        int lastNodeIndex = elementsNode.size() - 1;
        ObjectNode distanceNode = (ObjectNode) elementsNode.get(lastNodeIndex).get(DISTANCE);
        return new GoogleMapsDistanceResponse(distanceNode.get(VALUE_FIELD).asDouble());
    }
}
