package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Street;

public interface StreetService {
    Street getStreet(String streetName, String cityName);
}
