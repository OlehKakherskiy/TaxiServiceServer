package ua.kpi.mobiledev.util;

import ua.kpi.mobiledev.domain.TaxiDriver;
import ua.kpi.mobiledev.domain.User;

public interface LazyInitializationUtil {

    void initMobileNumbers(User user);

    void initCar(TaxiDriver taxiDriver);

    void initDriverLicense(TaxiDriver taxiDriver);
}
