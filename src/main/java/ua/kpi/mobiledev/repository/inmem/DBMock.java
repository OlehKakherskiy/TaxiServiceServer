package ua.kpi.mobiledev.repository.inmem;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.Car;
import ua.kpi.mobiledev.domain.DriverLicense;
import ua.kpi.mobiledev.domain.MobileNumber;
import ua.kpi.mobiledev.domain.Order;
import ua.kpi.mobiledev.domain.RoutePoint;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.web.security.model.Role;
import ua.kpi.mobiledev.web.security.model.SecurityDetails;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.Arrays.asList;
import static ua.kpi.mobiledev.domain.Car.CarType.PASSENGER_CAR;
import static ua.kpi.mobiledev.domain.User.UserType.CUSTOMER;

@Component("dbMock")
public class DBMock {

    private static User customer;
    private static TaxiDriver taxiDriver;

    static {
        List<MobileNumber> customerMobileNumbers = new ArrayList<>(
                asList(new MobileNumber(1, "+380975106619"),
                        new MobileNumber(2, "+380123456789")));

        customer = new User(1, "Customer, just Customer", "customer@gmail.com", CUSTOMER, customerMobileNumbers);

        List<MobileNumber> driverMobileNumbers = new ArrayList<>(asList(new MobileNumber(1, "+380987654321")));
        Car car = new Car(1, "DBR", "Aston Martin", "AA-0000-BB", 3, PASSENGER_CAR);
        DriverLicense driverLicense = new DriverLicense(1, "BXX 990664", LocalDate.now().plusYears(3), new byte[0], new byte[0]);
        taxiDriver = new TaxiDriver(2, "James Bond", "driver@gmail.com", driverMobileNumbers, car, driverLicense);
    }


    private ConcurrentHashMap<Integer, User> users;

    private ConcurrentHashMap<Long, Order> orders;

    private ConcurrentHashMap<String, UserDetails> userDetails;

    private AtomicLong orderIndex;
    private AtomicLong routePointIndex;

    private AtomicInteger userIndex;

    public DBMock() {
        orderIndex = new AtomicLong(0);
        userIndex = new AtomicInteger(2);
        routePointIndex = new AtomicLong(0);
        users = new ConcurrentHashMap<>();
        orders = new ConcurrentHashMap<>();
        userDetails = new ConcurrentHashMap<>();
        users.put(1,customer);
        users.put(2,taxiDriver);
        addUserDetails(prepareSecurityDetails(customer,"$argon2i$v=19$m=65536,t=2,p=1$a+mi/WKP7JyM73psXOaiuA$tqt2EhzNbSaWpN7vsXSKa6VG7CG7qPTmk0h41aEmtzU"));
        addUserDetails(prepareSecurityDetails(taxiDriver,"$argon2i$v=19$m=65536,t=2,p=1$a+mi/WKP7JyM73psXOaiuA$tqt2EhzNbSaWpN7vsXSKa6VG7CG7qPTmk0h41aEmtzU"));
    }

    private SecurityDetails prepareSecurityDetails(User user, String password) {
        return new SecurityDetails(user.getEmail(), password, "", true,
                asList(new Role(new SimpleGrantedAuthority("ROLE_"+user.getUserType().name()))));
    }

    public void addOrder(Order order) {
        Long index = orderIndex.incrementAndGet();
        orders.put(index, order);
        updateRoutePointIndexes(order);
        order.setOrderId(index);
    }

    private void updateRoutePointIndexes(Order order){
        order.getRoutePoints().forEach(this::setRoutePointId);
    }
    private void setRoutePointId(RoutePoint routePoint) {
        routePoint.setRoutePointId(routePointIndex.getAndIncrement());
    }

    public void addUser(User user) {
        Integer index = userIndex.incrementAndGet();
        users.put(index, user);
        user.setId(index);
    }

    public User getUser(Integer userIndex) {
        return users.get(userIndex);
    }

    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    public Order getOrder(Long orderIndex) {
        return orders.get(orderIndex);
    }

    public void replace(User user) {
        users.put(user.getId(), user);
    }

    public void replace(Order order) {
        orders.put(order.getOrderId(), order);
        updateRoutePointIndexes(order);
    }

    public List<Order> getOrders() {
        return new ArrayList<>(orders.values());
    }

    public UserDetails getUserDetails(String username) {
        return userDetails.get(username);
    }

    public void addUserDetails(UserDetails userDetails) {
        this.userDetails.put(userDetails.getUsername(), userDetails);
    }
}
