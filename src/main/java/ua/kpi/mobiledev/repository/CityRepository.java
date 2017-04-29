package ua.kpi.mobiledev.repository;

import org.springframework.data.repository.Repository;
import ua.kpi.mobiledev.domain.City;

@org.springframework.stereotype.Repository
public interface CityRepository extends Repository<City, Integer> {

    City getCityByNameAndArea(String cityName, String areaName, String countryName);
}
