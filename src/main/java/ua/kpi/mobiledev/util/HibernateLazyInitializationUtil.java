package ua.kpi.mobiledev.util;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

@Component
public class HibernateLazyInitializationUtil implements LazyInitializationUtil {

    @Override
    public void initMobileNumbers(User user) {
        Hibernate.initialize(user.getMobileNumbers());
    }

    @Override
    public void initCar(TaxiDriver taxiDriver) {
        Hibernate.initialize(taxiDriver.getCar());
    }
}
