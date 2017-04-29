package ua.kpi.mobiledev.repository;

import org.springframework.util.StringUtils;
import ua.kpi.mobiledev.domain.City;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public class CityRepositoryImpl implements CityRepository {

    private static final String selectWithArea = "Select city from City city where city.name =:cityName " +
            "And city.administrationArea.country.countryName=:countryName " +
            "And city.administrationArea.name = :areaName";

    private static final String selectWithoutArea = "Select city from City city where city.name =:cityName " +
            "And city.administrationArea.country.countryName=:countryName " +
            "And city.administrationArea.name is null";

    private static final String CITY_NAME = "cityName";
    private static final String AREA_NAME = "areaName";
    private static final String COUNTRY_NAME = "countryName";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public City getCityByNameAndArea(String cityName, String areaName, String countryName) {
        try {
            if (StringUtils.isEmpty(areaName)) {
                return (City) entityManager.createQuery(selectWithoutArea)
                        .setParameter(CITY_NAME, cityName)
                        .setParameter(COUNTRY_NAME, countryName)
                        .getSingleResult();
            } else {
                return (City) entityManager.createQuery(selectWithArea)
                        .setParameter(CITY_NAME, cityName)
                        .setParameter(AREA_NAME, areaName)
                        .setParameter(COUNTRY_NAME, countryName)
                        .getSingleResult();
            }
        } catch (NoResultException e) {
            return null;
        }
    }
}
