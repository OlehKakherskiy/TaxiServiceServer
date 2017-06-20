package ua.kpi.mobiledev.service.googlemaps;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.log4j.Logger;

import java.io.IOException;

public class GoogleMapsRouteDeserializer extends JsonDeserializer<GoogleMapsRouteResponse> {

    private static final Logger LOG = Logger.getLogger(GoogleMapsRouteDeserializer.class);

    private static final String ROWS = "rows";
    private static final String ELEMENTS = "elements";
    private static final String DISTANCE = "distance";
    private static final String DURATION = "duration";
    private static final int FIRST_NODE = 0;
    private static final String VALUE_FIELD = "value";

    @Override
    public GoogleMapsRouteResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        TreeNode tree = jsonParser.getCodec().readTree(jsonParser);
        if (LOG.isDebugEnabled()) {
            LOG.debug(tree);
        }
        TreeNode rowsElement = tree.get(ROWS);
        ArrayNode elementsNode = (ArrayNode) rowsElement.get(FIRST_NODE).get(ELEMENTS);
        ObjectNode elementNode = (ObjectNode) elementsNode.get(FIRST_NODE);
        return new GoogleMapsRouteResponse(getDistance(elementNode), getDuration(elementNode));
    }

    private int getDistance(ObjectNode elementNode) {
        return elementNode.get(DISTANCE).get(VALUE_FIELD).asInt();
    }

    private int getDuration(ObjectNode elementNode) {
        return elementNode.get(DURATION).get(VALUE_FIELD).asInt();
    }
}