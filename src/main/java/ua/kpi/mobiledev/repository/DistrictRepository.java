package ua.kpi.mobiledev.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import ua.kpi.mobiledev.domain.District;

public interface DistrictRepository extends Repository<District, Integer> {

    @Query("Select d from District d where d.districtName=:districtName and d.city.name=:cityName")
    District getByNameAndCityName(@Param("districtName") String districtName, @Param("cityName") String cityName);
}
