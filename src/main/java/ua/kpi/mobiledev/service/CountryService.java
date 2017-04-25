package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.Country;

import java.util.Optional;

public interface CountryService {
    Optional<Country> getCountry(String countryName);
}
