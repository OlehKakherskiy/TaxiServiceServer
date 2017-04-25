package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.City;

import java.util.Optional;

public interface CityService {
    Optional<City> getCity(String cityName, String adminAreaName, String countryName);
}
