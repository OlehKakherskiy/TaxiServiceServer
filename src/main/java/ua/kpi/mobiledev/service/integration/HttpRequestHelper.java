package ua.kpi.mobiledev.service.integration;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component("httpRequestHelper")
public class HttpRequestHelper {

    public <T> T processGetRequest(String url, Class<T> resultType, Map<String, ?> variables) {
        return getRestTemplate().getForObject(url, resultType, variables);
    }

    public <T> void processPostRequest(String url, Map<String, String> headers, T body) {
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.entrySet().forEach(entry -> httpHeaders.add(entry.getKey(), entry.getValue()));

        HttpEntity<T> request = new HttpEntity<T>(body, httpHeaders);
        getRestTemplate().postForEntity(url, request, Void.class);
    }

    protected RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

}