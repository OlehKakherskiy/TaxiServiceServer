package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.repository.StreetRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StreetServiceTest {

    private static final String STREET = "Street";
    private static final String CITY = "City";
    private static final Street STREET_MOCK = mock(Street.class);

    @Mock
    private StreetRepository streetRepository;

    private StreetServiceImpl streetService;

    @Before
    public void setUp() throws Exception {
        streetService = new StreetServiceImpl();
        streetService.setStreetRepository(streetRepository);
    }

    @Test
    public void shouldReturnStreet() {
        when(streetRepository.customGet(STREET, CITY)).thenReturn(STREET_MOCK);

        assertThat(streetService.getStreet(STREET, CITY), is(STREET_MOCK));
    }
}