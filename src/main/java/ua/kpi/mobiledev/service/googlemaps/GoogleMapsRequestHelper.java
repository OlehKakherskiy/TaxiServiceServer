package ua.kpi.mobiledev.service.googlemaps;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component("googleMapsRequestHelper")
public class GoogleMapsRequestHelper {

    public <T> T processGetRequest(String url, Class<T> resultType, Map<String, ?> variables) {
        return getRestTemplate().getForObject(url, resultType, variables);
    }

    protected RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}