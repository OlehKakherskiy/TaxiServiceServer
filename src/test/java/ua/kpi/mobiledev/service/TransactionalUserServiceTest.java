package ua.kpi.mobiledev.service;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.exception.RequestException;
import ua.kpi.mobiledev.exception.SystemException;
import ua.kpi.mobiledev.repository.UserRepository;
import ua.kpi.mobiledev.testCategories.UnitTest;
import ua.kpi.mobiledev.util.LazyInitializationUtil;
import ua.kpi.mobiledev.web.security.model.Role;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;
import ua.kpi.mobiledev.web.security.service.CustomUserDetailsService;

import java.time.LocalDate;
import java.util.ArrayList;

import static java.time.LocalDate.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@Category(UnitTest.class)
@RunWith(MockitoJUnitRunner.class)
public class TransactionalUserServiceTest {

    private static final Integer NO_USER_ID = null;
    private static final Integer USER_ID = 1;
    private static final Integer TAXI_DRIVER_ID = 2;
    private static final String PASSWORD = "password";
    private static final String USER_EMAIL = "userEmail";
    private static final String CUSTOMER_NAME = "username";
    private static final String TAXI_DRIVER_NAME = "taxiDriverName";
    private static final String TAXI_DRIVER_EMAIL = "taxiDriverEmail";
    private static final int CAR_ID = 1;

    @Mock
    private UserRepository<User, Integer> userRepositoryMock;
    @Mock
    private LazyInitializationUtil lazyInitializationUtilMock;
    @Mock
    private CustomUserDetailsService customUserDetailsServiceMock;
    @Mock
    private DriverLicense mockDriverLicense;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private UserService userServiceUnderTest;
    private User userToRegister = getUserMock(NO_USER_ID, User.UserType.CUSTOMER);
    private User userBeforeUpdate;
    private Car carBeforeUpdate;

    @Before
    public void setUp() throws Exception {
        userServiceUnderTest = new TransactionalUserService(userRepositoryMock,
                customUserDetailsServiceMock, lazyInitializationUtilMock);

        userBeforeUpdate = getUserMock(USER_ID, User.UserType.CUSTOMER);
        carBeforeUpdate = createCarBeforeUpdate();
        addDefaultMobileNumbers(userBeforeUpdate);
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
    public void shouldUpdateUserName() {
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);
        expectedUser.setName("userName2");
        addDefaultMobileNumbers(expectedUser);
        User userToUpdate = new User(USER_ID, "userName2", null, null, null);
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(userBeforeUpdate);
        when(userRepositoryMock.save(expectedUser)).thenReturn(expectedUser);

        assertThat(userServiceUnderTest.update(userToUpdate), is(expectedUser));
        verify(userRepositoryMock).findOne(USER_ID);
        verify(userRepositoryMock).save(expectedUser);
    }

    @Test
    public void shouldNotUpdateUserEmail() {
        expectedException.expect(RequestException.class);
        User userToUpdate = getUserMock(USER_ID, User.UserType.CUSTOMER);
        userToUpdate.setEmail("emailToUpdate@gmail.com");

        userServiceUnderTest.update(userToUpdate);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void shouldNotUpdateUserType() {
        expectedException.expect(RequestException.class);
        User userToUpdate = getUserMock(USER_ID, User.UserType.CUSTOMER);
        userToUpdate.setEmail(null);

        userServiceUnderTest.update(userToUpdate);
        verifyNoMoreInteractions(userRepositoryMock);
    }

    @Test
    public void shouldUpdateExistedMobileNumber() {
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);
        MobileNumber changedMobileNumber = new MobileNumber(1, "num1_changed");
        expectedUser.getMobileNumbers().addAll(asList(changedMobileNumber, new MobileNumber(2, "num2")));
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(userBeforeUpdate);
        when(userRepositoryMock.save(expectedUser)).thenReturn(expectedUser);
        User userToUpdate = new User(USER_ID, null, null, null, asList(changedMobileNumber));

        assertThat(userServiceUnderTest.update(userToUpdate), is(expectedUser));
        verify(userRepositoryMock).findOne(USER_ID);
        verify(userRepositoryMock).save(expectedUser);
    }

    @Test
    public void shouldRemoveMobileNumberWhenNumberIsNull() {
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);
        expectedUser.setMobileNumbers(new ArrayList<>(asList(new MobileNumber(2, "num2"))));

        User userToUpdate = new User(USER_ID, null, null, null,
                asList(new MobileNumber(1, null)));
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(userBeforeUpdate);
        when(userRepositoryMock.save(expectedUser)).thenReturn(expectedUser);

        assertThat(userServiceUnderTest.update(userToUpdate), is(expectedUser));
        verify(userRepositoryMock).findOne(USER_ID);
        verify(userRepositoryMock).save(expectedUser);
    }

    @Test
    public void shouldAddMobileNumberWhenUpdate() {
        User expectedUser = getUserMock(USER_ID, User.UserType.CUSTOMER);
        addDefaultMobileNumbers(expectedUser);
        MobileNumber newNumber = new MobileNumber(null, "newMobileNumber");
        MobileNumber newNumber2 = new MobileNumber(null, "newMobileNumber2");
        expectedUser.getMobileNumbers().addAll(asList(newNumber, newNumber2));
        User userToUpdate = new User(USER_ID, null, null, null, new ArrayList<>(asList(newNumber, newNumber2)));
        when(userRepositoryMock.findOne(USER_ID)).thenReturn(userBeforeUpdate);
        when(userRepositoryMock.save(expectedUser)).thenReturn(expectedUser);

        assertThat(userServiceUnderTest.update(userToUpdate), is(expectedUser));
        verify(userRepositoryMock).findOne(USER_ID);
        verify(userRepositoryMock).save(expectedUser);
    }

    @Test
    public void shouldUpdateCarModelWhenUserIsDriver() {
        TaxiDriver userBeforeUpdate = (TaxiDriver) getUserMock(TAXI_DRIVER_ID, User.UserType.TAXI_DRIVER);

        TaxiDriver expectedUser = (TaxiDriver) getUserMock(TAXI_DRIVER_ID, User.UserType.TAXI_DRIVER);
        Car updatedCar = new Car(CAR_ID, "model", "manufacturer",
                "plateNumber", 3, Car.CarType.PASSENGER_CAR);
        expectedUser.setCar(updatedCar);

        TaxiDriver userToUpdate = new TaxiDriver();
        userToUpdate.setId(TAXI_DRIVER_ID);
        Car carToUpdate = new Car(CAR_ID, "model", "manufacturer",
                "plateNumber", 3, Car.CarType.PASSENGER_CAR);
        userToUpdate.setCar(carToUpdate);

        when(userRepositoryMock.findOne(TAXI_DRIVER_ID)).thenReturn(userBeforeUpdate);
        when(userRepositoryMock.save(expectedUser)).thenReturn(expectedUser);

        assertThat(userServiceUnderTest.update(userToUpdate), is(expectedUser));
        verify(userRepositoryMock).findOne(TAXI_DRIVER_ID);
        verify(userRepositoryMock).save(expectedUser);
    }

    @Test
    public void shouldUpdateDriverLicenceWhenUserIsDriver() {
        DriverLicense driverLicenseBeforeUpdate = new DriverLicense(1, "driverLicence", now().minusDays(1),
                null, null);
        TaxiDriver userBeforeUpdate = (TaxiDriver) getUserMock(TAXI_DRIVER_ID, User.UserType.TAXI_DRIVER);
        userBeforeUpdate.setDriverLicense(driverLicenseBeforeUpdate);

        LocalDate newDate = now().plusYears(1L);
        DriverLicense driverLicenseAfterUpdate = new DriverLicense(1, "afterUpdate", newDate, null, null);
        TaxiDriver userAfterUpdate = (TaxiDriver) getUserMock(TAXI_DRIVER_ID, User.UserType.TAXI_DRIVER);
        userAfterUpdate.setCar(carBeforeUpdate);
        userAfterUpdate.setDriverLicense(driverLicenseAfterUpdate);

        DriverLicense driverLicenseToUpdate = new DriverLicense(1, "afterUpdate", newDate,
                null, null);
        TaxiDriver toUpdate = new TaxiDriver(TAXI_DRIVER_ID, null, null, null, null, driverLicenseToUpdate);
        toUpdate.setUserType(null);

        when(userRepositoryMock.findOne(TAXI_DRIVER_ID)).thenReturn(userBeforeUpdate);
        when(userRepositoryMock.save(userAfterUpdate)).thenReturn(userAfterUpdate);

        assertThat(userServiceUnderTest.update(toUpdate), is(userAfterUpdate));
        verify(userRepositoryMock).findOne(TAXI_DRIVER_ID);
        verify(userRepositoryMock).save(userAfterUpdate);

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
                singletonList(new Role(new SimpleGrantedAuthority(user.getUserType().name()))));
    }

    @Test(expected = RequestException.class)
    public void shouldThrowIllegalArgumentExceptionWhenUserAlreadyExists() throws Exception {

        UserDetails existedUserDetails = mock(UserDetails.class);
        when(customUserDetailsServiceMock.fullLoadWithAuthorities(USER_EMAIL)).thenReturn(existedUserDetails);

        userServiceUnderTest.register(userToRegister, PASSWORD);

    }

    private User getUserMock(Integer id, User.UserType userType) {
        if (userType == User.UserType.TAXI_DRIVER) {
            return new TaxiDriver(id, TAXI_DRIVER_NAME, TAXI_DRIVER_EMAIL, new ArrayList<>(),
                    carBeforeUpdate, mockDriverLicense);
        } else {
            return new User(id, CUSTOMER_NAME, USER_EMAIL, User.UserType.CUSTOMER, new ArrayList<>());
        }
    }

    private void addDefaultMobileNumbers(User userToInjectMobileNumbers) {
        userToInjectMobileNumbers.setMobileNumbers(new ArrayList(asList(
                new MobileNumber(1, "num1"),
                new MobileNumber(2, "num2"))));
    }

    private Car createCarBeforeUpdate() {
        Car carBeforeUpdate = new Car();
        carBeforeUpdate.setCarId(CAR_ID);
        carBeforeUpdate.setPlateNumber("plateNum");
        carBeforeUpdate.setSeatNumber(3);
        carBeforeUpdate.setModel("model");
        carBeforeUpdate.setManufacturer("manufacturer");
        carBeforeUpdate.setCarType(Car.CarType.PASSENGER_CAR);
        return carBeforeUpdate;
    }

}