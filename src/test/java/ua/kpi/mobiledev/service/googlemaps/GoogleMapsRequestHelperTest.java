package ua.kpi.mobiledev.service.googlemaps;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class GoogleMapsRequestHelperTest {

    private static final String RESPONSE = "response";
    private static final String URL = "testUrl";
    private static final Class<String> RESPONSE_TYPE = String.class;
    private static final Map<String, Object> URL_VARIABLES = emptyMap();

    @Test
    public void shouldMakeGetCall() {
        RestTemplate mockRestCall = mock(RestTemplate.class);
        when(mockRestCall.getForObject(URL, RESPONSE_TYPE, URL_VARIABLES)).thenReturn(RESPONSE);
        GoogleMapsRequestHelper requestHelper = spy(new GoogleMapsRequestHelper());
        doReturn(mockRestCall).when(requestHelper).getRestTemplate();

        assertThat(requestHelper.processGetRequest(URL, RESPONSE_TYPE, URL_VARIABLES), is(RESPONSE));
        verify(requestHelper).getRestTemplate();
        verify(mockRestCall).getForObject(URL, RESPONSE_TYPE, URL_VARIABLES);
    }
}