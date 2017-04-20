package ua.kpi.mobiledev.repository.inmem;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.repository.CityRepository;

@Repository("cityRepository")
public class InMemCityRepository implements CityRepository {

    @Override
    public City getCityByNameAndArea(String cityName, String areaName) {
        return null;
    }
}
