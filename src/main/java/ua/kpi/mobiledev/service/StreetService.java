package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Street;

import java.util.Optional;

public interface StreetService {
    Optional<Street> getStreet(String streetName, String cityName);
}
