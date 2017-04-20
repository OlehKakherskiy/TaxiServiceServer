package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ua.kpi.mobiledev.domain.Address;
import ua.kpi.mobiledev.domain.Street;
import ua.kpi.mobiledev.repository.AddressRepository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AddressServiceTest {

    private static final String STREET_NAME = "StreetName";
    private static final String HOUSE_NUM = "houseNumber";
    private static final Street STREET_MOCK = mock(Street.class);
    private static final Address ADDRESS = new Address(1, HOUSE_NUM, STREET_MOCK);
    private static final Address NOT_PERSISTED_ADDRESS = new Address(null, HOUSE_NUM, STREET_MOCK);

    @Mock
    private AddressRepository addressRepository;

    private TransactionalAddressService addressService;

    @Before
    public void setUp() throws Exception {
        addressService = new TransactionalAddressService();
        addressService.setAddressRepository(addressRepository);
    }

    @Test
    public void shouldGetExistedAddress() {
        when(addressRepository.customGet(STREET_NAME, HOUSE_NUM)).thenReturn(ADDRESS);

        assertThat(addressService.getAddress(STREET_NAME, HOUSE_NUM).get(), is(ADDRESS));
        verify(addressRepository).customGet(STREET_NAME, HOUSE_NUM);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    public void shouldReturnNullIfNoAddressExists() {
        when(addressRepository.customGet(STREET_NAME, HOUSE_NUM)).thenReturn(null);

        assertThat(addressService.getAddress(STREET_NAME, HOUSE_NUM).isPresent(), is(false));
        verify(addressRepository).customGet(STREET_NAME, HOUSE_NUM);
        verifyNoMoreInteractions(addressRepository);
    }

    @Test
    public void shouldAddAddress() {
        when(addressRepository.save(NOT_PERSISTED_ADDRESS)).thenReturn(ADDRESS);

        assertThat(addressService.addAddress(NOT_PERSISTED_ADDRESS), is(ADDRESS));
        verify(addressRepository).save(NOT_PERSISTED_ADDRESS);
        verifyNoMoreInteractions(addressRepository);
    }
}