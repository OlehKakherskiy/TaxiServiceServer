package ua.kpi.mobiledev.service.integration;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component("httpRequestHelper")
public class HttpRequestHelper {

    private static final Logger LOG = Logger.getLogger(HttpRequestHelper.class);

    public <T> T processGetRequest(String url, Class<T> resultType, Map<String, ?> variables) {
        return getRestTemplate().getForObject(url, resultType, variables);
    }

    public <T> void processPostRequest(String url, Map<String, String> headers, T body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::add);

        HttpEntity<T> request = new HttpEntity<>(body, httpHeaders);
        try {
            getRestTemplate().postForEntity(url, request, Void.class);
        } catch (RuntimeException e) {
            LOG.error("exception during sending notification with structure: " + request.toString(), e);
        }
    }

    protected RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}