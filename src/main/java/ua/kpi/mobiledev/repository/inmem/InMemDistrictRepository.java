package ua.kpi.mobiledev.repository.inmem;

import org.springframework.stereotype.Repository;
import ua.kpi.mobiledev.domain.District;
import ua.kpi.mobiledev.repository.DistrictRepository;

@Repository("districtRepository")
public class InMemDistrictRepository implements DistrictRepository {

    @Override
    public District getByNameAndCityName(String districtName, String cityName) {
        return null;
    }

}
