package ua.kpi.mobiledev.repository;

import org.springframework.data.repository.Repository;
import ua.kpi.mobiledev.domain.District;

@org.springframework.stereotype.Repository
public interface DistrictRepository extends Repository<District, Integer> {

    District getByNameAndCityName(String districtName, String cityName);
}
