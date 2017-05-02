package ua.kpi.mobiledev.service.integration;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

public class HttpRequestHelperTest {

    private static final String RESPONSE = "response";
    private static final String URL = "testUrl";
    private static final Class<String> RESPONSE_TYPE = String.class;
    private static final Map<String, Object> URL_VARIABLES = emptyMap();

    @Test
    public void shouldMakeGetCall() {
        RestTemplate mockRestCall = mock(RestTemplate.class);
        when(mockRestCall.getForObject(URL, RESPONSE_TYPE, URL_VARIABLES)).thenReturn(RESPONSE);
        HttpRequestHelper requestHelper = spy(new HttpRequestHelper());
        doReturn(mockRestCall).when(requestHelper).getRestTemplate();

        assertThat(requestHelper.processGetRequest(URL, RESPONSE_TYPE, URL_VARIABLES), is(RESPONSE));
        verify(requestHelper).getRestTemplate();
        verify(mockRestCall).getForObject(URL, RESPONSE_TYPE, URL_VARIABLES);
    }
}