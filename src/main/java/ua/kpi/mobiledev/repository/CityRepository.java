package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ua.kpi.mobiledev.domain.City;

@org.springframework.stereotype.Repository
public interface CityRepository extends Repository<City, Integer> {

    @Query("Select city from City city where city.name =:cityName " +
            "And city.administrationArea.country.countryName=:countryName " +
            "And city.administrationArea.name = :areaName")
    City getCityByNameAndArea(@Param("cityName") String cityName, @Param("areaName") String areaName, @Param("countryName") String countryName);
}
