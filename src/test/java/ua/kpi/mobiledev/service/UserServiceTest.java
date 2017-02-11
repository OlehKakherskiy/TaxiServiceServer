package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.exception.SystemException;
import ua.kpi.mobiledev.repository.UserRepository;
import ua.kpi.mobiledev.testCategories.UnitTest;
import ua.kpi.mobiledev.util.LazyInitializationUtil;
import ua.kpi.mobiledev.web.security.model.Role;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;
import ua.kpi.mobiledev.web.security.service.CustomUserDetailsService;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class UserServiceTest {

    private static final Integer NO_USER_ID = null;
    private static final Integer USER_ID = 1;
    private static final Integer TAXI_DRIVER_ID = 2;
    private static final String PASSWORD = "password";
    private static final String USER_EMAIL = "userEmail";

    @Mock
    private UserRepository userRepositoryMock;
    @Mock
    private LazyInitializationUtil lazyInitializationUtilMock;
    @Mock
    private CustomUserDetailsService customUserDetailsServiceMock;
    @Mock
    private Car mockCar;

    private UserService userServiceUnderTest;
    private User userToRegister = getUserMock(NO_USER_ID, User.UserType.CUSTOMER);

    @Before
    public void setUp() throws Exception {
        userServiceUnderTest = new TransactionalUserService(userRepositoryMock,
                customUserDetailsServiceMock, lazyInitializationUtilMock);
    }

    @Test
    public void getExistedUserById() {
        //when
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(getUserMock(USER_ID, User.UserType.CUSTOMER));

        //then
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);
        User actualUser = userServiceUnderTest.getById(USER_ID);
        assertEquals(expectedUser, actualUser);
        verify(userRepositoryMock).findOne(USER_ID);
        verify(lazyInitializationUtilMock).initMobileNumbers(any());
        verifyNoMoreInteractions(userRepositoryMock, lazyInitializationUtilMock, customUserDetailsServiceMock);
    }

    @Test
    public void getExistedTaxiDriverById() {
        //when
        when(userRepositoryMock.findOne(TAXI_DRIVER_ID)).thenReturn(getUserMock(TAXI_DRIVER_ID, User.UserType.TAXI_DRIVER));

        //then
        User expectedUser = getUserMock(TAXI_DRIVER_ID, User.UserType.TAXI_DRIVER);
        User actualUser = userServiceUnderTest.getById(TAXI_DRIVER_ID);
        assertEquals(expectedUser, actualUser);
        verify(userRepositoryMock).findOne(TAXI_DRIVER_ID);
        verify(lazyInitializationUtilMock).initMobileNumbers(any());
        verify(lazyInitializationUtilMock).initCar(any());
        verifyNoMoreInteractions(userRepositoryMock, lazyInitializationUtilMock, customUserDetailsServiceMock);
    }

    @Test
    public void getExistedUserByEmail() {
        when(userRepositoryMock.findByEmail(USER_EMAIL)).thenReturn(getUserMock(USER_ID, User.UserType.CUSTOMER));
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);

        User actualUser = userServiceUnderTest.getByUsername(USER_EMAIL);

        assertEquals(expectedUser, actualUser);
        verify(userRepositoryMock).findByEmail(anyString());
    }

    @Test
    public void getNotExistedUserByEmail() {
        when(userRepositoryMock.findByEmail(any())).thenReturn(null);

        assertNull(userServiceUnderTest.getByUsername(USER_EMAIL));
        verify(userRepositoryMock).findByEmail(anyString());
    }

    @Test
    public void shouldUpdateUser() {
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);
        User userToUpdate = getUserMock(USER_ID, User.UserType.CUSTOMER);
        when(userRepositoryMock.save(any(User.class))).thenReturn(getUserMock(USER_ID, User.UserType.CUSTOMER));

        User actualUser = userServiceUnderTest.update(userToUpdate);

        assertEquals(expectedUser, actualUser);
        verify(userRepositoryMock).save(any(User.class));
    }

    @Test
    public void shouldRegisterNewUser() {
        User userToRegister = getUserMock(NO_USER_ID, User.UserType.CUSTOMER);
        User userAfterRegister = getUserMock(USER_ID, User.UserType.CUSTOMER);
        SecurityDetails userDetails = createUserDetails(userAfterRegister, PASSWORD);
        when(customUserDetailsServiceMock.fullLoadWithAuthorities(any())).thenReturn(null);
        when(userRepositoryMock.save(userToRegister)).thenReturn(userAfterRegister);
        doNothing().when(customUserDetailsServiceMock).registerNewUser(userDetails);

        User expected = getUserMock(USER_ID, User.UserType.CUSTOMER);
        User afterCompleteRegisterUser = userServiceUnderTest.register(userToRegister, PASSWORD);

        assertEquals(expected, afterCompleteRegisterUser);
        verify(customUserDetailsServiceMock).fullLoadWithAuthorities(USER_EMAIL);
        verify(userRepositoryMock).save(userToRegister);
        verify(customUserDetailsServiceMock).registerNewUser(any());
    }

    @Test(expected = SystemException.class)
    public void shouldThrowSystemExceptionIfWereProblemsDuringUserSavingToDb() throws Exception {
        User userToRegister = getUserMock(NO_USER_ID, User.UserType.CUSTOMER);
        when(customUserDetailsServiceMock.fullLoadWithAuthorities(USER_EMAIL)).thenReturn(null);
        when(userRepositoryMock.save(userToRegister)).thenReturn(null);

        userServiceUnderTest.register(userToRegister, PASSWORD);
    }

    private SecurityDetails createUserDetails(User user, String password) {
        return new SecurityDetails(user.getEmail(), password, "", true,
                Arrays.asList(new Role(new SimpleGrantedAuthority(user.getUserType().name()))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionWhenUserAlreadyExists() throws Exception {

        UserDetails existedUserDetails = mock(UserDetails.class);
        when(customUserDetailsServiceMock.fullLoadWithAuthorities(USER_EMAIL)).thenReturn(existedUserDetails);

        userServiceUnderTest.register(userToRegister, PASSWORD);

    }

    private User getUserMock(Integer id, User.UserType userType) {
        if (userType == User.UserType.TAXI_DRIVER) {
            return new TaxiDriver(id, "taxiDriverName", "taxiDriverEmail", Collections.emptySet(), mockCar);
        } else {
            return new User(id, "username", USER_EMAIL, User.UserType.CUSTOMER, Collections.emptySet());
        }
    }

}