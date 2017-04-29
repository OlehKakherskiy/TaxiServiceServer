package ua.kpi.mobiledev.repository;

import org.springframework.util.StringUtils;
import ua.kpi.mobiledev.domain.District;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

public class DistrictRepositoryImpl implements DistrictRepository {

    private static final String SELECT_WITH_DISTRICT_NAME = "Select d from District d where d.districtName=:districtName and d.city.name=:cityName";
    private static final String SELECT_WITH_NO_DISTRICT_NAME = "Select d from District d where d.city.name=:cityName and d.districtName is Null";

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public District getByNameAndCityName(String districtName, String cityName) {
        try {
            if (StringUtils.isEmpty(districtName)) {
                return (District) entityManager.createQuery(SELECT_WITH_NO_DISTRICT_NAME)
                        .setParameter("cityName", cityName)
                        .getSingleResult();
            } else {
                return (District) entityManager.createQuery(SELECT_WITH_DISTRICT_NAME)
                        .setParameter("cityName", cityName)
                        .setParameter("districtName", districtName)
                        .getSingleResult();
            }
        } catch (NoResultException e) {
            return null;
        }
    }
}
