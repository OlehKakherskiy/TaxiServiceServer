package ua.kpi.mobiledev.service;

import org.springframework.stereotype.Service;
import ua.kpi.mobiledev.domain.City;
import ua.kpi.mobiledev.repository.CityRepository;

import javax.annotation.Resource;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Service("cityService")
public class CityServiceImpl implements CityService {

    @Resource(name = "cityRepository")
    private CityRepository cityRepository;

    @Override
    public Optional<City> getCity(String cityName, String adminAreaName) {
        return ofNullable(cityRepository.getCityByNameAndArea(cityName, adminAreaName));
    }
}
