package ua.kpi.mobiledev.service;

import ua.kpi.mobiledev.domain.District;

import java.util.Optional;

public interface DistrictService {
    Optional<District> get(String districtName, String cityName);
}
